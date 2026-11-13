package com.example.auction.client.controller;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {
    private static SoundManager instance;

    // Trình phát nhạc nền (Chỉ có 1 bài chạy cùng lúc)
    private MediaPlayer bgmPlayer;

    // Bộ nhớ đệm (Cache) cho các hiệu ứng âm thanh ngắn để không phải load lại file nhiều lần
    private final Map<String, AudioClip> sfxCache = new HashMap<>();

    // Cài đặt âm lượng mặc định (0.0 đến 1.0)
    private double bgmVolume = 0.3; // Nhạc nền nên để nhỏ để không át tiếng khác
    private double sfxVolume = 0.8; // Tiếng hiệu ứng để to hơn

    private boolean isMuted = false;

    private SoundManager() {}

    public static synchronized SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    // ==========================================
    // 1. NHẠC NỀN (BACKGROUND MUSIC)
    // ==========================================
    public void playBGM(String fileName) {
        try {
            URL resource = getClass().getResource("/sounds/" + fileName);
            if (resource == null) {
                System.err.println("❌ Không tìm thấy nhạc nền: " + fileName);
                return;
            }

            // Nếu đang phát nhạc cũ thì tắt đi
            if (bgmPlayer != null) {
                bgmPlayer.stop();
                bgmPlayer.dispose();
            }

            Media media = new Media(resource.toExternalForm());
            bgmPlayer = new MediaPlayer(media);

            // Lặp lại vô tận
            bgmPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            bgmPlayer.setVolume(isMuted ? 0 : bgmVolume);
            bgmPlayer.play();

        } catch (Exception e) {
            System.err.println("❌ Lỗi phát BGM: " + e.getMessage());
        }
    }

    public void stopBGM() {
        if (bgmPlayer != null) {
            bgmPlayer.stop();
        }
    }

    // ==========================================
    // 2. HIỆU ỨNG ÂM THANH (SOUND EFFECTS)
    // ==========================================
    public void playSFX(String fileName) {
        if (isMuted) return;

        try {
            // Lấy từ Cache ra, nếu chưa có thì mới load file (Giúp app chạy cực mượt)
            AudioClip clip = sfxCache.computeIfAbsent(fileName, k -> {
                URL resource = getClass().getResource("/sounds/" + fileName);
                if (resource == null) {
                    System.err.println("❌ Không tìm thấy SFX: " + fileName);
                    return null;
                }
                return new AudioClip(resource.toExternalForm());
            });

            if (clip != null) {
                clip.setVolume(sfxVolume);
                clip.play();
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi phát SFX: " + e.getMessage());
        }
    }

    // ==========================================
    // 3. ĐIỀU KHIỂN ÂM LƯỢNG (DÙNG CHO NÚT CÀI ĐẶT)
    // ==========================================
    public void toggleMute() {
        isMuted = !isMuted;
        if (bgmPlayer != null) {
            bgmPlayer.setVolume(isMuted ? 0 : bgmVolume);
        }
    }

    public boolean isMuted() {
        return isMuted;
    }
}