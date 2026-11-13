package com.example.auction.client.view;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;
import javafx.scene.paint.Color; // Thêm dòng import này ở đầu file
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ClockUtil {

    // Hàm này nhận vào 1 cái Label bất kỳ và biến nó thành đồng hồ
    public static void bindClockToLabel(Label clockLabel) {
        if (clockLabel == null) return;
        clockLabel.setTextFill(Color.WHITE);
        // Định dạng thời gian: Ngày/Tháng/Năm Giờ:Phút:Giây (Giống web xịn)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy  HH:mm:ss");

        // Tạo một Timeline lặp lại mỗi 1 giây
        Timeline clock = new Timeline(
                new KeyFrame(Duration.ZERO, e -> {
                    // Cứ mỗi nhịp, lấy giờ hệ thống hiện tại nhét vào Label
                    String currentTime = LocalDateTime.now().format(formatter);
                    clockLabel.setText(currentTime);
                }),
                new KeyFrame(Duration.seconds(1))
        );

        // Cho đồng hồ chạy vô hạn
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }
}