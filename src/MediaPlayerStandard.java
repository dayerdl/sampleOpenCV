import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.Player;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

/**
 * @author BUDDHIMA
 */

public class MediaPlayerStandard extends javax.swing.JPanel {

    public MediaPlayerStandard(URL mediauUrl) {

        setLayout(new BorderLayout());

        try {

            Player mediaPlayer = Manager.createRealizedPlayer(new MediaLocator(mediauUrl));

            Component video = mediaPlayer.getVisualComponent();

            Component control = mediaPlayer.getControlPanelComponent();

            if (video != null) {
                setSize((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(), (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight());
                add(video, BorderLayout.CENTER);
                //setUndecorated(true);
                setVisible(true);
                //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            } // add video component

            add(control, BorderLayout.SOUTH);            // place the control in  panel

            mediaPlayer.start();

        } catch (Exception e) {

        }

    }

    public static void main(String[] args) {

        JFileChooser fileChooser = new JFileChooser();

        fileChooser.showOpenDialog(null);

        URL mediaUrl = null;

        try {

            mediaUrl = fileChooser.getSelectedFile().toURI().toURL();

        } catch (MalformedURLException ex) {

            System.out.println(ex);

        }

        JFrame mediaTest = new JFrame("Movie Player");

        mediaTest.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        MediaPlayerStandard mediaPanel = new MediaPlayerStandard(mediaUrl);

        mediaTest.add(mediaPanel);

        mediaTest.setSize(800, 700); // set the size of the player

        mediaTest.setLocationRelativeTo(null);

        mediaTest.setVisible(true);

    }

}
