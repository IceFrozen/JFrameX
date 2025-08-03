package cn.ximuli.jframex.ui.internalJFrame;

import cn.ximuli.jframex.model.User;
import cn.ximuli.jframex.ui.component.menu.Mate;
import cn.ximuli.jframex.ui.test.InputKeyListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


@Mate(value = "app.menu.user.internal.userService", icon = "icons/user_detail", order = 10, id = "app.menu.user.internal.userService")
@Slf4j
public class UserDetailJFrame extends JInternalFrame {
    private User user;

    public UserDetailJFrame(User user) {
        this.user = user;
        log.info("user: {}", user);
        setIconifiable(true);
        setClosable(true);
        setTitle("客户信息管理");
        final UserDetailPanel userDetailPanel = new UserDetailPanel();
        add(userDetailPanel);
        pack();
    }

    public class UserDetailPanel extends JPanel {
        private JTextField keHuQuanCheng;
        private JTextField yinHangZhangHao;
        private JTextField kaiHuYinHang;
        private JTextField EMail;
        private JTextField lianXiDianHua;
        private JTextField lianXiRen;
        private JTextField chuanZhen;
        private JTextField dianHua;
        private JTextField youZhengBianMa;
        private JTextField diZhi;
        private JTextField keHuJianCheng;
        private JButton resetButton;

        public UserDetailPanel() {
            super();
            setBounds(10, 10, 460, 300);
            setLayout(new GridBagLayout());
            setVisible(true);
            final JLabel khName = new JLabel();
            khName.setText("客户全称�?");
            setupComponet(khName, 0, 0, 1, 0, false);
            keHuQuanCheng = new JTextField();
            // 定位全称文本�?
            setupComponet(keHuQuanCheng, 1, 0, 3, 350, true);
            final JLabel addressLabel = new JLabel("客户地址�?");
            setupComponet(addressLabel, 0, 1, 1, 0, false);
            diZhi = new JTextField();
            // 定位地址文本�?
            setupComponet(diZhi, 1, 1, 3, 0, true);
            final JLabel jc = new JLabel();
            jc.setText("客户简称：");
            setupComponet(jc, 0, 2, 1, 0, false);
            keHuJianCheng = new JTextField();
            // 定位客户简称文�?�?
            setupComponet(keHuJianCheng, 1, 2, 1, 100, true);
            setupComponet(new JLabel("�?政编码："), 2, 2, 1, 0, false);
            youZhengBianMa = new JTextField();
            // 定位�?政编码文�?�?
            setupComponet(youZhengBianMa, 3, 2, 1, 100, true);
            youZhengBianMa.addKeyListener(new InputKeyListener());
            setupComponet(new JLabel("电话�?"), 0, 3, 1, 0, false);
            dianHua = new JTextField();
            // 定位电话文本�?
            setupComponet(dianHua, 1, 3, 1, 100, true);
            dianHua.addKeyListener(new InputKeyListener());
            setupComponet(new JLabel("传真�?"), 2, 3, 1, 0, false);
            chuanZhen = new JTextField();
            // 定位传真文本�?
            chuanZhen.addKeyListener(new InputKeyListener());
            setupComponet(chuanZhen, 3, 3, 1, 100, true);
            setupComponet(new JLabel("联系人："), 0, 4, 1, 0, false);
            lianXiRen = new JTextField();
            // 定位联系人文�?�?
            setupComponet(lianXiRen, 1, 4, 1, 100, true);
            setupComponet(new JLabel("联系电话�?"), 2, 4, 1, 0, false);
            lianXiDianHua = new JTextField();
            // 定位联系电话文本�?
            setupComponet(lianXiDianHua, 3, 4, 1, 100, true);
            lianXiDianHua.addKeyListener(new InputKeyListener());
            setupComponet(new JLabel("E-Mail�?"), 0, 5, 1, 0, false);
            EMail = new JTextField();
            // 定位E-Mail文本�?
            setupComponet(EMail, 1, 5, 3, 350, true);
            setupComponet(new JLabel("开户银行："), 0, 6, 1, 0, false);
            kaiHuYinHang = new JTextField();
            // 定位开户银行文�?�?
            setupComponet(kaiHuYinHang, 1, 6, 1, 100, true);
            setupComponet(new JLabel("银�?�账号："), 2, 6, 1, 0, false);
            yinHangZhangHao = new JTextField();
            // 定位银�?�账号文�?�?
            setupComponet(yinHangZhangHao, 3, 6, 1, 100, true);
            final JButton saveButton = new JButton("保存");
            // 定位保存按钮
            setupComponet(saveButton, 1, 7, 1, 0, false);
            //saveButton.addActionListener(new cn.ximuli.jframex.ui.test.KeHuTianJiaPanel.SaveButtonActionListener());
            resetButton = new JButton("重置");
            // 定位重置按钮
            setupComponet(resetButton, 3, 7, 1, 0, false);
            //resetButton.addActionListener(new cn.ximuli.jframex.ui.test.KeHuTianJiaPanel.ChongZheButtonActionListener());
        }

        // 设置组件位置并添加到容器�?
        private void setupComponet(JComponent component, int gridx, int gridy,
                                   int gridwidth, int ipadx, boolean fill) {
            final GridBagConstraints gridBagConstrains = new GridBagConstraints();
            gridBagConstrains.gridx = gridx;
            gridBagConstrains.gridy = gridy;
            gridBagConstrains.insets = new Insets(5, 1, 3, 1);
            if (gridwidth > 1)
                gridBagConstrains.gridwidth = gridwidth;
            if (ipadx > 0)
                gridBagConstrains.ipadx = ipadx;
            if (fill)
                gridBagConstrains.fill = GridBagConstraints.HORIZONTAL;
            add(component, gridBagConstrains);
        }

        // 保存按钮的事件监�?�?
        private final class SaveButtonActionListener implements ActionListener {
            public void actionPerformed(final ActionEvent e) {
                if (diZhi.getText().equals("")
                        || youZhengBianMa.getText().equals("")
                        || chuanZhen.getText().equals("")
                        || yinHangZhangHao.getText().equals("")
                        || keHuJianCheng.getText().equals("")
                        || keHuQuanCheng.getText().equals("")
                        || lianXiRen.getText().equals("")
                        || lianXiDianHua.getText().equals("")
                        || EMail.getText().equals("")
                        || dianHua.getText().equals("")
                        || kaiHuYinHang.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "请填写全部信�?");
                    return;
                }
//            ResultSet haveUser = Dao
//                    .query("select * from tb_khinfo where khname='"
//                            + keHuQuanCheng.getText().trim() + "'");
//            try {
//                if (haveUser.next()){
//                    System.out.println("error");
//                    JOptionPane.showMessageDialog(KeHuTianJiaPanel.this,
//                            "客户信息添加失败，存在同名�?�户", "客户添加信息",
//                            JOptionPane.INFORMATION_MESSAGE);
//                    return;
//                }
//            } catch (Exception er) {
//                er.printStackTrace();
//            }
//            ResultSet set = Dao.query("select max(id) from tb_khinfo");
//            String id = null;
//            try {
//                if (set != null && set.next()) {
//                    String sid = set.getString(1);
//                    if (sid == null)
//                        id = "kh1001";
//                    else {
//                        String str = sid.substring(2);
//                        id = "kh" + (Integer.parseInt(str) + 1);
//                    }
//                }
//            } catch (SQLException e1) {
//                e1.printStackTrace();
//            }
//            TbKhinfo khinfo = new TbKhinfo();
//            khinfo.setId(id);
//            khinfo.setAddress(diZhi.getText().trim());
//            khinfo.setBianma(youZhengBianMa.getText().trim());
//            khinfo.setFax(chuanZhen.getText().trim());
//            khinfo.setHao(yinHangZhangHao.getText().trim());
//            khinfo.setJian(keHuJianCheng.getText().trim());
//            khinfo.setKhname(keHuQuanCheng.getText().trim());
//            khinfo.setLian(lianXiRen.getText().trim());
//            khinfo.setLtel(lianXiDianHua.getText().trim());
//            khinfo.setMail(EMail.getText().trim());
//            khinfo.setTel(dianHua.getText().trim());
//            khinfo.setXinhang(kaiHuYinHang.getText());
//            Dao.addKeHu(khinfo);
//            JOptionPane.showMessageDialog(KeHuTianJiaPanel.this, "已成功添加�?�户",
//                    "客户添加信息", JOptionPane.INFORMATION_MESSAGE);
//            resetButton.doClick();
            }
        }

        // 重置按钮的事件监�?�?
        private class ChongZheButtonActionListener implements ActionListener {
            public void actionPerformed(final ActionEvent e) {
                keHuQuanCheng.setText("");
                yinHangZhangHao.setText("");
                kaiHuYinHang.setText("");
                EMail.setText("");
                lianXiDianHua.setText("");
                lianXiRen.setText("");
                chuanZhen.setText("");
                dianHua.setText("");
                youZhengBianMa.setText("");
                diZhi.setText("");
                keHuJianCheng.setText("");
            }
        }
    }

}
