package com.owjk.xiaoshuo.domain.pipeline

import com.owjk.xiaoshuo.data.repository.BookRepository
import com.owjk.xiaoshuo.domain.agent.ArchitectAgent
import com.owjk.xiaoshuo.domain.agent.AuditorAgent
import com.owjk.xiaoshuo.domain.agent.ReviserAgent
import com.owjk.xiaoshuo.domain.agent.SummaryAgent
import com.owjk.xiaoshuo.domain.agent.WriterAgent
import com.owjk.xiaoshuo.domain.model.PipelineEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class WritingPipeline @Inject constructor(
    private val architectAgent: ArchitectAgent,
    private val writerAgent: WriterAgent,
    private val auditorAgent: AuditorAgent,
    private val reviserAgent: ReviserAgent,
    private val summaryAgent: SummaryAgent,
    private val bookRepository: BookRepository
) {
    /**
     * 全自动写作流水线：策划 → 写作（流式）→ 审核 → 修改 → 摘要 → 保存
     */
    fun writeNextChapter(bookId: Long): Flow<PipelineEvent> = flow {
        try {
            val context = bookRepository.getStoryContext(bookId)
            val contextText = context.toPromptText()

            // 1. 策划大纲
            emit(PipelineEvent.Stage("策划大纲中..."))
            val outline = architectAgent.call(contextText)

            // 2. 写作正文（流式）
            emit(PipelineEvent.Stage("正在生成章节内容..."))
            val draftBuilder = StringBuilder()
            writerAgent.callStream(
                "【故事背景】\n$contextText\n\n【本章大纲】\n$outline\n\n请根据大纲写出完整的章节正文："
            ).collect { chunk ->
                draftBuilder.append(chunk)
                emit(PipelineEvent.StreamChunk(chunk))
            }
            val draft = draftBuilder.toString()

            // 3. 审核
            emit(PipelineEvent.Stage("审核连续性..."))
            val auditResult = auditorAgent.audit(draft, contextText)

            // 4. 如有问题，修改一次
            val finalContent = if (!auditResult.passed && auditResult.issues.isNotEmpty()) {
                emit(PipelineEvent.Stage("修正审核问题..."))
                val revisePrompt = "【原始草稿】\n$draft\n\n【审核意见】\n${auditResult.suggestions}\n\n请修改："
                reviserAgent.call(revisePrompt)
            } else {
                draft
            }

            // 5. 生成摘要
            emit(PipelineEvent.Stage("生成章节摘要..."))
            val summary = summaryAgent.summarize(finalContent)

            // 6. 保存
            emit(PipelineEvent.Stage("保存章节..."))
            val chapterTitle = extractTitle(finalContent, context.nextChapterNumber)
            bookRepository.saveChapter(
                bookId = bookId,
                chapterNumber = context.nextChapterNumber,
                title = chapterTitle,
                content = finalContent,
                summary = summary
            )

            emit(PipelineEvent.Done)
        } catch (e: Exception) {
            emit(PipelineEvent.Error(e.message ?: "未知错误"))
        }
    }

    /**
     * 仅 AI 续写（流式），不走完整流水线，供手动编辑时快速使用
     */
    fun quickContinue(bookId: Long, userHint: String = ""): Flow<PipelineEvent> = flow {
        try {
            val context = bookRepository.getStoryContext(bookId)
            val contextText = context.toPromptText()

            emit(PipelineEvent.Stage("AI 续写中..."))
            val prompt = if (userHint.isNotBlank()) {
                "【故事背景】\n$contextText\n\n【续写提示】\n$userHint\n\n请直接续写："
            } else {
                "【故事背景】\n$contextText\n\n请直接续写下一段内容："
            }
            writerAgent.callStream(prompt).collect { chunk ->
                emit(PipelineEvent.StreamChunk(chunk))
            }
            emit(PipelineEvent.Done)
        } catch (e: Exception) {
            emit(PipelineEvent.Error(e.message ?: "未知错误"))
        }
    }

    private fun extractTitle(content: String, chapterNumber: Int): String {
        val firstLine = content.lines().firstOrNull { it.isNotBlank() } ?: ""
        return if (firstLine.startsWith("第") && firstLine.length < 30) {
            firstLine.trim()
        } else {
            "第${chapterNumber}章"
        }
    }
}
