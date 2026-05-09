package com.crawl.qeeccdata.service;

import com.crawl.qeeccdata.model.CrawlSong;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Layer1 - 索引发现层
 * 爬取各版块列表页, 提取歌曲 songId + 歌手 + 歌名
 *
 * 两种模式:
 * - 模式A: 直接列表页(新歌榜/TOP榜单/DJ舞曲/子榜单) → 翻页提取歌曲
 * - 模式B: 两层钻取(歌手/歌单/电台) → 先拿列表, 再进详情页拿歌曲
 */
@Service
public class IndexDiscovery {

    private final QeeccHttpClient httpClient;

    /** 所有版块定义: 版块名 → 入口URL路径 */
    private static final Map<String, String> DIRECT_SECTIONS = new LinkedHashMap<>();
    static {
        // 模式A: 直接列表页
        DIRECT_SECTIONS.put("新歌榜", "/list/new.html");
        DIRECT_SECTIONS.put("TOP榜单", "/list/top.html");
        DIRECT_SECTIONS.put("DJ舞曲", "/list/djwuqu.html");
        // 子榜单
        DIRECT_SECTIONS.put("抖音热歌榜", "/list/douyin.html");
        DIRECT_SECTIONS.put("快手热歌榜", "/list/kuaishou.html");
        DIRECT_SECTIONS.put("音乐热评榜", "/list/share.html");
        DIRECT_SECTIONS.put("音乐先锋榜", "/list/ndtop.html");
        DIRECT_SECTIONS.put("爱听电音榜", "/list/hktop.html");
        DIRECT_SECTIONS.put("车载歌曲榜", "/list/cztop.html");
        DIRECT_SECTIONS.put("酷我飙升榜", "/list/kuwo.html");
        DIRECT_SECTIONS.put("电音热歌榜", "/list/dytop.html");
        DIRECT_SECTIONS.put("ACG新歌榜", "/list/newacg.html");
        DIRECT_SECTIONS.put("综艺新歌榜", "/list/newzy.html");
        DIRECT_SECTIONS.put("说唱先锋榜", "/list/sctop.html");
        DIRECT_SECTIONS.put("影视金曲榜", "/list/ystop.html");
        DIRECT_SECTIONS.put("粤语金曲榜", "/list/yytop.html");
        DIRECT_SECTIONS.put("欧美金曲榜", "/list/ustop.html");
        DIRECT_SECTIONS.put("80后热歌榜", "/list/blhot.html");
        DIRECT_SECTIONS.put("网红新歌榜", "/list/wlhot.html");
        DIRECT_SECTIONS.put("古风音乐榜", "/list/gfhot.html");
        DIRECT_SECTIONS.put("KTV点唱榜", "/list/ktvtop.html");
        DIRECT_SECTIONS.put("网络红歌榜", "/list/hot.html");
        DIRECT_SECTIONS.put("英国排行榜", "/list/ygtop.html");
        DIRECT_SECTIONS.put("韩国排行榜", "/list/krtop.html");
        DIRECT_SECTIONS.put("日本排行榜", "/list/jptop.html");
    }

    /** 两层钻取版块 */
    private static final Map<String, String> TWO_LEVEL_SECTIONS = new LinkedHashMap<>();
    static {
        TWO_LEVEL_SECTIONS.put("歌手", "/singerlist/index/index/index/index.html");
        TWO_LEVEL_SECTIONS.put("歌单", "/playtype/index.html");
        TWO_LEVEL_SECTIONS.put("电台", "/radiolist/index.html");
        TWO_LEVEL_SECTIONS.put("高清MV", "/mvlist/index.html");
    }

    public IndexDiscovery(QeeccHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    // ==================== 模式A: 直接列表页 ====================

    /**
     * 爬取某个直接列表版块的所有歌曲(自动翻页)
     * 返回发现的歌曲列表
     */
    public List<CrawlSong> discoverDirectSection(String sectionName, String entryPath,
                                                  java.util.function.Consumer<String> onProgress) {
        List<CrawlSong> songs = new ArrayList<>();
        String baseUrl = httpClient.getBaseUrl();

        int page = 1;
        while (true) {
            // 构造分页URL: /list/new.html 或 /list/new/2.html
            String pageUrl;
            if (page == 1) {
                pageUrl = baseUrl + entryPath;
            } else {
                // 从 /list/new.html 提取 /list/new
                String basePath = entryPath.replace(".html", "");
                pageUrl = baseUrl + basePath + "/" + page + ".html";
            }

            if (onProgress != null) onProgress.accept("[" + sectionName + "] 爬取第 " + page + " 页: " + pageUrl);
            System.out.println("[Discovery] [" + sectionName + "] 第 " + page + " 页: " + pageUrl);

            Document doc = httpClient.fetchPage(pageUrl);
            if (doc == null) break;

            // 提取歌曲列表: .play_list ul li .name a
            Elements items = doc.select(".play_list ul li .name a");
            if (items.isEmpty()) {
                // 也可能没有 .play_list, 尝试其他选择器
                items = doc.select(".lkmusic_list ul li .name a");
            }

            int beforeSize = songs.size();
            for (Element item : items) {
                String href = item.attr("href");
                String text = item.text().trim();
                if (href != null && href.contains("/song/") && !text.isEmpty()) {
                    String songId = httpClient.extractSongId(href);
                    String[] parsed = httpClient.parseSongText(text);
                    songs.add(CrawlSong.forIndex(songId, parsed[1], parsed[0],
                            sectionName, sectionName, href, text));
                }
            }

            int newSongs = songs.size() - beforeSize;
            System.out.println("[Discovery] [" + sectionName + "] 第 " + page + " 页发现 " + newSongs + " 首歌曲");

            // 检查是否还有下一页
            if (!hasNextPage(doc)) break;

            page++;
            httpClient.rateLimit();
        }

        System.out.println("[Discovery] [" + sectionName + "] 共发现 " + songs.size() + " 首歌曲");
        return songs;
    }

    /**
     * 判断是否有下一页
     */
    private boolean hasNextPage(Document doc) {
        Elements pageLinks = doc.select(".page a");
        for (Element link : pageLinks) {
            String text = link.text().trim();
            if (text.contains("下一页") || text.contains("尾页")) {
                return link.hasAttr("href");
            }
        }
        return false;
    }

    /**
     * 获取某个版块的总页数
     */
    public int getTotalPages(String entryPath) {
        String baseUrl = httpClient.getBaseUrl();
        Document doc = httpClient.fetchPage(baseUrl + entryPath);
        if (doc == null) return 1;

        Elements pageLinks = doc.select(".page a");
        int maxPage = 1;
        for (Element link : pageLinks) {
            String text = link.text().trim();
            // 尝试解析为数字
            try {
                int pageNum = Integer.parseInt(text);
                maxPage = Math.max(maxPage, pageNum);
            } catch (NumberFormatException ignored) {}

            // 尾页链接: href="/list/top/5.html"
            String href = link.attr("href");
            if (text.contains("尾页") && href != null && !href.isEmpty()) {
                try {
                    // 从URL提取页码
                    String[] parts = href.split("/");
                    String lastPart = parts[parts.length - 1].replace(".html", "");
                    int lastPage = Integer.parseInt(lastPart);
                    maxPage = Math.max(maxPage, lastPage);
                } catch (Exception ignored) {}
            }
        }
        return maxPage;
    }

    // ==================== 模式B: 两层钻取 ====================

    /**
     * 爬取歌手列表页 → 返回歌手ID和名称
     */
    public List<SingerInfo> discoverSingerList(String entryPath, int maxPages,
                                                java.util.function.Consumer<String> onProgress) {
        List<SingerInfo> singers = new ArrayList<>();
        String baseUrl = httpClient.getBaseUrl();

        for (int page = 1; page <= maxPages; page++) {
            String pageUrl;
            if (page == 1) {
                pageUrl = baseUrl + entryPath;
            } else {
                String basePath = entryPath.replace(".html", "");
                pageUrl = baseUrl + basePath + "/" + page + ".html";
            }

            if (onProgress != null) onProgress.accept("[歌手列表] 第 " + page + "/" + maxPages + " 页");
            Document doc = httpClient.fetchPage(pageUrl);
            if (doc == null) break;

            // 歌手列表: .singer_list ul li .name a
            Elements items = doc.select(".singer_list ul li .name a");
            for (Element item : items) {
                String href = item.attr("href");
                String name = item.text().trim();
                if (href != null && href.contains("/singer/") && !name.isEmpty()) {
                    String singerId = href.substring(href.lastIndexOf("/singer/") + "/singer/".length());
                    singerId = singerId.replace(".html", "");
                    singers.add(new SingerInfo(singerId, name, href));
                }
            }

            System.out.println("[Discovery] [歌手列表] 第 " + page + " 页, 累计 " + singers.size() + " 个歌手");
            httpClient.rateLimit();
        }

        return singers;
    }

    /**
     * 进入歌手详情页, 提取该歌手的所有歌曲
     */
    public List<CrawlSong> discoverSingerSongs(SingerInfo singer,
                                                java.util.function.Consumer<String> onProgress) {
        List<CrawlSong> songs = new ArrayList<>();
        String baseUrl = httpClient.getBaseUrl();
        String singerUrl = baseUrl + singer.href();

        if (onProgress != null) onProgress.accept("[歌手] " + singer.name());
        Document doc = httpClient.fetchPage(singerUrl);
        if (doc == null) return songs;

        // 歌手详情页的歌曲: .play_list ul li .name a
        Elements items = doc.select(".play_list ul li .name a");
        if (items.isEmpty()) {
            items = doc.select(".singer_list ul li .name a");
        }

        for (Element item : items) {
            String href = item.attr("href");
            String text = item.text().trim();
            if (href != null && href.contains("/song/") && !text.isEmpty()) {
                String songId = httpClient.extractSongId(href);
                String[] parsed = httpClient.parseSongText(text);
                songs.add(CrawlSong.forIndex(songId, parsed[1], parsed[0],
                        "歌手", "歌手/" + singer.name(), href, text));
            }
        }

        System.out.println("[Discovery] [歌手] " + singer.name() + " → " + songs.size() + " 首歌曲");
        return songs;
    }

    /**
     * 爬取歌单列表 → 返回歌单ID和名称
     */
    public List<PlaylistInfo> discoverPlaylistList(String entryPath, int maxPages,
                                                    java.util.function.Consumer<String> onProgress) {
        List<PlaylistInfo> playlists = new ArrayList<>();
        String baseUrl = httpClient.getBaseUrl();

        for (int page = 1; page <= maxPages; page++) {
            String pageUrl;
            if (page == 1) {
                pageUrl = baseUrl + entryPath;
            } else {
                String basePath = entryPath.replace(".html", "").replace("/index", "");
                pageUrl = baseUrl + basePath + "/" + page + ".html";
            }

            if (onProgress != null) onProgress.accept("[歌单列表] 第 " + page + "/" + maxPages + " 页");
            Document doc = httpClient.fetchPage(pageUrl);
            if (doc == null) break;

            // 歌单列表: .video_list ul.play li
            Elements items = doc.select(".video_list ul.play li");
            for (Element item : items) {
                Element link = item.selectFirst(".name a");
                if (link != null) {
                    String href = link.attr("href");
                    String name = link.text().trim();
                    if (href != null && href.contains("/playlist/") && !name.isEmpty()) {
                        String playlistId = href.substring(href.lastIndexOf("/playlist/") + "/playlist/".length());
                        playlistId = playlistId.replace(".html", "");
                        playlists.add(new PlaylistInfo(playlistId, name, href));
                    }
                }
            }

            System.out.println("[Discovery] [歌单列表] 第 " + page + " 页, 累计 " + playlists.size() + " 个歌单");
            httpClient.rateLimit();
        }

        return playlists;
    }

    /**
     * 进入歌单详情页, 提取歌单中的所有歌曲
     */
    public List<CrawlSong> discoverPlaylistSongs(PlaylistInfo playlist,
                                                  java.util.function.Consumer<String> onProgress) {
        List<CrawlSong> songs = new ArrayList<>();
        String baseUrl = httpClient.getBaseUrl();
        String playlistUrl = baseUrl + playlist.href();

        if (onProgress != null) onProgress.accept("[歌单] " + playlist.name());
        Document doc = httpClient.fetchPage(playlistUrl);
        if (doc == null) return songs;

        Elements items = doc.select(".play_list ul li .name a");
        for (Element item : items) {
            String href = item.attr("href");
            String text = item.text().trim();
            if (href != null && href.contains("/song/") && !text.isEmpty()) {
                String songId = httpClient.extractSongId(href);
                String[] parsed = httpClient.parseSongText(text);
                songs.add(CrawlSong.forIndex(songId, parsed[1], parsed[0],
                        "歌单", "歌单/" + playlist.name(), href, text));
            }
        }

        System.out.println("[Discovery] [歌单] " + playlist.name() + " → " + songs.size() + " 首歌曲");
        return songs;
    }

    // ==================== Getter ====================

    public Map<String, String> getDirectSections() {
        return DIRECT_SECTIONS;
    }

    public Map<String, String> getTwoLevelSections() {
        return TWO_LEVEL_SECTIONS;
    }

    // ==================== 数据结构 ====================

    public record SingerInfo(String singerId, String name, String href) {}
    public record PlaylistInfo(String playlistId, String name, String href) {}
}
