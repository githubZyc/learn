package com.crawl.qeecc.rag;

import com.crawl.qeecc.service.DanceMusicCrawler;
import jakarta.annotation.PostConstruct;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * RAG 层 - 歌曲知识库
 * 将爬取的歌曲列表构建为内存向量存储，支持语义相似度检索
 * 启动时自动加载 qeecc.com 的歌曲数据并生成 Embedding
 */
@Service
public class SongRagService {

    private final DanceMusicCrawler danceMusicCrawler;
    private final EmbeddingModel embeddingModel;
    private final List<SongVector> vectorStore = new ArrayList<>();

    public SongRagService(DanceMusicCrawler danceMusicCrawler, EmbeddingModel embeddingModel) {
        this.danceMusicCrawler = danceMusicCrawler;
        this.embeddingModel = embeddingModel;
    }

    @PostConstruct
    public void init() {
        loadSongs();
    }

    private void loadSongs() {
        try {
            List<DanceMusicCrawler.DanceMusicCategory> categories = danceMusicCrawler.findDanceMusicCategories();
            for (DanceMusicCrawler.DanceMusicCategory category : categories) {
                List<DanceMusicCrawler.AudioTrack> tracks = danceMusicCrawler.getAudioTracksFromCategory(category.getUrl());
                for (DanceMusicCrawler.AudioTrack track : tracks) {
                    String content = String.format("歌曲: %s, 分类: %s", track.getTitle(), category.getName());
                    float[] embedding = embed(content);
                    vectorStore.add(new SongVector(track.getTitle(), category.getName(), track.getAudioUrl(), embedding));
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
        return scored.stream().limit(topK).map(s -> new SongResult(s.song().title(), s.song().category(), s.song().url(), s.score())).toList();
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

    private record SongVector(String title, String category, String url, float[] embedding) {}
    private record ScoredSong(SongVector song, float score) {}
    public record SongResult(String title, String category, String url, float score) {}
}
