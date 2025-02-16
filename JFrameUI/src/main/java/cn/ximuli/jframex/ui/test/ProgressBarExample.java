package cn.ximuli.jframex.ui.test;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ProgressBarExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("ProgressBar Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setLayout(new FlowLayout());

        // 创建进度条
        JProgressBar progressBar = new JProgressBar(0, 100); // 最小值为 0，最大值为 100
        progressBar.setStringPainted(true); // 显示进度数值
        frame.add(progressBar);
        frame.setVisible(true);

        // 使用 SwingWorker 执行耗时任务
        SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
            @Override
            protected Void doInBackground() throws Exception {
                for (int i = 0; i <= 100; i++) {
                    Thread.sleep(100); // 模拟耗时任务
                    publish(i); // 发布进度
                }
                return null;
            }

            @Override
            protected void process(List<Integer> chunks) {
                int progress = chunks.get(chunks.size() - 1); // 获取最新进度
                progressBar.setValue(progress); // 更新进度值
                progressBar.setString("Loading... " + progress + "%"); // 更新显示文本
            }

            @Override
            protected void done() {
                progressBar.setString("Done!"); // 任务完成
            }
        };

        worker.execute(); // 启动任务
    }
}