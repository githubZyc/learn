package com.crawl.qeecc.rag;

import com.crawl.qeecc.service.QeeccSearchService;
import jakarta.annotation.PostConstruct;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * RAG 层 - 歌曲知识库
 * 利用站内搜索接口获取歌曲列表，构建内存向量存储，支持语义相似度检索
 * 启动时自动加载热门歌曲数据并生成 Embedding
 */
@Service
public class SongRagService {

    private final QeeccSearchService searchService;
    private final EmbeddingModel embeddingModel;
    private final List<SongVector> vectorStore = new ArrayList<>();

    public SongRagService(QeeccSearchService searchService, EmbeddingModel embeddingModel) {
        this.searchService = searchService;
        this.embeddingModel = embeddingModel;
    }

    @PostConstruct
    public void init() {
        loadSongs();
    }

    private void loadSongs() {
        try {
            // 使用热门关键词预加载歌曲数据到向量存储
            String[] hotKeywords = {"江南", "晴天", "海阔天空", "成都", "偏爱", "泡沫", "十年"};
            for (String keyword : hotKeywords) {
                List<QeeccSearchService.SearchResult> results = searchService.searchSongs(keyword);
                for (QeeccSearchService.SearchResult sr : results) {
                    String content = sr.singer() + " " + sr.songName();
                    float[] embedding = embed(content);
                    vectorStore.add(new SongVector(sr.songName(), sr.singer(), sr.songId(), embedding));
                }
            }
            System.out.println("[RAG] 已加载 " + vectorStore.size() + " 首歌曲到向量存储");
        } catch (Exception e) {
            System.out.println("[RAG] 加载歌曲失败: " + e.getMessage());
        }
    }

    private float[] embed(String text) {
        EmbeddingRequest request = new EmbeddingRequest(List.of(text), null);
        EmbeddingResponse response = embeddingModel.call(request);
        return response.getResults().get(0).getOutput();
    }

    public List<SongResult> search(String query, int topK) {
        if (vectorStore.isEmpty()) {
            return List.of();
        }

        float[] queryEmbedding = embed(query);

        List<ScoredSong> scored = new ArrayList<>();
        for (SongVector song : vectorStore) {
            float similarity = cosineSimilarity(queryEmbedding, song.embedding());
            scored.add(new ScoredSong(song, similarity));
        }

        scored.sort((a, b) -> Float.compare(b.score(), a.score()));
        return scored.stream().limit(topK).map(s -> new SongResult(s.song().title(), s.song().singer(), s.song().songId(), s.score())).toList();
    }

    private float cosineSimilarity(float[] a, float[] b) {
        double dot = 0, normA = 0, normB = 0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        return (float) (dot / (Math.sqrt(normA) * Math.sqrt(normB)));
    }

    private record SongVector(String title, String singer, String songId, float[] embedding) {}
    private record ScoredSong(SongVector song, float score) {}
    public record SongResult(String title, String singer, String songId, float score) {}
}
