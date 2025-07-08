package cn.ximuli.jframex.ui.component;

import cn.ximuli.jframex.ui.I18nHelper;
import cn.ximuli.jframex.ui.manager.ResourceLoaderManager;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.components.FlatTriStateCheckBox;
import com.formdev.flatlaf.util.HSLColor;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.HierarchyEvent;
import java.util.function.Function;

@Component
@Slf4j
public class ExtrasInternalJFrame extends CommonInternalJFrame {
    private JPanel jPanel;

    public ExtrasInternalJFrame(ResourceLoaderManager resources, JDesktopPane desktopPane) {
        super(resources, desktopPane);
        setTitle(I18nHelper.getMessage("app.menu.view.components.extras"));
        setFrameIcon(super.resources.getIcon("icons/extras_component"));
        // Add component listener to adjust size when shown
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                adjustFrameSize();
            }
        });
    }

    @Override
    protected void initUI() {
        jPanel = new ExtrasPanel(super.resources);
        jPanel.setBorder(LineBorder.createGrayLineBorder());
        setLayout(new BorderLayout());
        add(jPanel, BorderLayout.CENTER);
    }

    private void adjustFrameSize() {
        // Get the preferred size of the jPanel
        Dimension panelSize = jPanel.getPreferredSize();
        // Add some padding for the frame's borders and title bar
        int padding = 20;
        int width = panelSize.width + padding;
        int height = panelSize.height + padding;

        // Ensure the size doesn't exceed the desktop pane's dimensions
        Dimension desktopSize = desktopPane.getSize();
        width = Math.min(width, desktopSize.width - 20);
        height = Math.min(height, desktopSize.height - 20);

        // Set the frame size
        setSize(width, height);

        // Center the frame within the desktop pane
        int x = (desktopSize.width - width) / 2;
        int y = (desktopSize.height - height) / 2;
        setLocation(x, y);
    }

    public class ExtrasPanel extends JPanel {
        private Timer rainbowIconTimer;
        private int rainbowCounter = 0;
        private ResourceLoaderManager resources;
        // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
        private JLabel label4;
        private JLabel label1;
        private FlatTriStateCheckBox triStateCheckBox1;
        private JLabel triStateLabel1;
        private JLabel label2;
        private JPanel svgIconsPanel;
        private JLabel label3;
        private JSeparator separator1;
        private JLabel label5;
        private JLabel label6;
        private JLabel rainbowIcon;
        private JLabel label7;
        private JToggleButton redToggleButton;
        private JToggleButton brighterToggleButton;
        // JFormDesigner - End of variables declaration  //GEN-END:variables

        public ExtrasPanel(ResourceLoaderManager resources) {
            this.resources = resources;
            initComponents(resources);

            triStateLabel1.setText( triStateCheckBox1.getState().toString() );

            addSVGIcon( "actions/copy" );
            addSVGIcon( "actions/colors" );
            addSVGIcon( "actions/execute" );
            addSVGIcon( "actions/suspend" );
            addSVGIcon( "actions/intentionBulb" );
            addSVGIcon( "actions/quickfixOffBulb" );

            addSVGIcon( "objects/abstractClass" );
            addSVGIcon( "objects/abstractMethod" );
            addSVGIcon( "objects/annotationtype" );
            addSVGIcon( "objects/annotationtype" );
            addSVGIcon( "objects/css" );
            addSVGIcon( "objects/javaScript" );
            addSVGIcon( "objects/xhtml" );

            addSVGIcon( "errorDialog" );
            addSVGIcon( "informationDialog" );
            addSVGIcon( "warningDialog" );

            initRainbowIcon();
        }

        private void initRainbowIcon() {

            FlatSVGIcon icon = (FlatSVGIcon) resources.getIcon("extras/svg/informationDialog");
            icon.setColorFilter( new FlatSVGIcon.ColorFilter(color -> {
                rainbowCounter += 1;
                rainbowCounter %= 255;
                return Color.getHSBColor( rainbowCounter / 255f, 1, 1 );
            } ) );
            rainbowIcon.setIcon( icon );

            rainbowIconTimer = new Timer( 30, e -> {
                rainbowIcon.repaint();
            } );

            // start rainbow timer only if panel is shown ("Extras" tab is active)
            addHierarchyListener( e -> {
                if( e.getID() == HierarchyEvent.HIERARCHY_CHANGED &&
                        (e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 )
                {
                    if( isShowing() )
                        rainbowIconTimer.start();
                    else
                        rainbowIconTimer.stop();
                }
            } );
        }

        private void addSVGIcon( String name ) {
            svgIconsPanel.add( new JLabel( resources.getIcon("extras/svg/" + name)));
        }

        private void triStateCheckBox1Changed() {
            triStateLabel1.setText( triStateCheckBox1.getState().toString() );
        }

        private void redChanged() {
            brighterToggleButton.setSelected( false );

            Function<Color, Color> mapper = null;
            if( redToggleButton.isSelected() ) {
                float[] redHSL = HSLColor.fromRGB( Color.red );
                mapper = color -> {
                    float[] hsl = HSLColor.fromRGB( color );
                    return HSLColor.toRGB( redHSL[0], 70, hsl[2] );
                };
            }
            FlatSVGIcon.ColorFilter.getInstance().setMapper( mapper );

            // repaint whole application window because global color filter also affects
            // icons in menubar, toolbar, etc.
            SwingUtilities.windowForComponent( this ).repaint();
        }

        private void brighterChanged() {
            redToggleButton.setSelected( false );

            FlatSVGIcon.ColorFilter.getInstance().setMapper( brighterToggleButton.isSelected()
                    ? color -> color.brighter().brighter()
                    : null );

            // repaint whole application window because global color filter also affects
            // icons in menubar, toolbar, etc.
            SwingUtilities.windowForComponent( this ).repaint();
        }

        private void initComponents(ResourceLoaderManager resources) {
            // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            label4 = new JLabel();
            label1 = new JLabel();
            triStateCheckBox1 = new FlatTriStateCheckBox();
            triStateLabel1 = new JLabel();
            label2 = new JLabel();
            svgIconsPanel = new JPanel();
            label3 = new JLabel();
            separator1 = new JSeparator();
            label5 = new JLabel();
            label6 = new JLabel();
            rainbowIcon = new JLabel();
            label7 = new JLabel();
            redToggleButton = new JToggleButton();
            brighterToggleButton = new JToggleButton();

            //======== this ========
            setLayout(new MigLayout(
                    "insets dialog,hidemode 3",
                    // columns
                    "[]" +
                            "[]" +
                            "[left]",
                    // rows
                    "[]para" +
                            "[]" +
                            "[]" +
                            "[]" +
                            "[]" +
                            "[]" +
                            "[]" +
                            "[]"));

            //---- label4 ----
            label4.setText("Note: Components on this page require the flatlaf-extras library.");
            add(label4, "cell 0 0 3 1");

            //---- label1 ----
            label1.setText("TriStateCheckBox:");
            add(label1, "cell 0 1");

            //---- triStateCheckBox1 ----
            triStateCheckBox1.setText("Three States");
            triStateCheckBox1.addActionListener(e -> triStateCheckBox1Changed());
            add(triStateCheckBox1, "cell 1 1");

            //---- triStateLabel1 ----
            triStateLabel1.setText("text");
            triStateLabel1.setEnabled(false);
            add(triStateLabel1, "cell 2 1,gapx 30");

            //---- label2 ----
            label2.setText("SVG Icons:");
            add(label2, "cell 0 2");

            //======== svgIconsPanel ========
            {
                svgIconsPanel.setLayout(new MigLayout(
                        "insets 0,hidemode 3",
                        // columns
                        "[fill]",
                        // rows
                        "[grow,center]"));
            }
            add(svgIconsPanel, "cell 1 2 2 1");

            //---- label3 ----
            label3.setText("The icons may change colors when switching to another theme.");
            add(label3, "cell 1 3 2 1");
            add(separator1, "cell 1 4 2 1,growx");

            //---- label5 ----
            label5.setText("Color filters can be also applied to icons. Globally or for each instance.");
            add(label5, "cell 1 5 2 1");

            //---- label6 ----
            label6.setText("Rainbow color filter");
            add(label6, "cell 1 6 2 1");
            add(rainbowIcon, "cell 1 6 2 1");

            //---- label7 ----
            label7.setText("Global icon color filter");
            add(label7, "cell 1 7 2 1");

            //---- redToggleButton ----
            redToggleButton.setText("Toggle RED");
            redToggleButton.addActionListener(e -> redChanged());
            add(redToggleButton, "cell 1 7 2 1");

            //---- brighterToggleButton ----
            brighterToggleButton.setText("Toggle brighter");
            brighterToggleButton.addActionListener(e -> brighterChanged());
            add(brighterToggleButton, "cell 1 7 2 1");
            // JFormDesigner - End of component initialization  //GEN-END:initComponents
        }

    }

}


