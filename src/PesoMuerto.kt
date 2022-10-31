import javafx.application.Application
import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.beans.property.DoubleProperty
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.layout.StackPane
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.scene.media.MediaView
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.util.Duration
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.opencv.videoio.VideoCapture
import org.opencv.videoio.VideoWriter
import org.opencv.videoio.Videoio
import java.io.File
import java.net.URL
import javax.swing.JFrame


object PesoMuerto : Application() {
    @JvmStatic
    fun main(args: Array<String>) {
        //Compulsory -> Load Native libraries
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
        println("Loaded OpenCV version " + Core.VERSION)
        //detectObjectInVideo()
        val url = "file:/Users/davidlopez.dayer/SampleOpenCV/src/video_out.mp4"
        Platform.runLater(Runnable {
            // do your GUI stuff here
            start(Stage())
        })
    }

    private fun detectObjectInVideo() {

        val capture = VideoCapture("/Users/davidlopez.dayer/SampleOpenCV/src/peso-muerto.mp4")

        var frmCount = 0.0
        if (capture.isOpened) {
            frmCount = capture.get(Videoio.CAP_PROP_FRAME_COUNT)
            println("frmCount = $frmCount")
            // CAP_PROP_FRAME_COUNT
        } else {
            println("Capture is not opened!")
            return
        }

        // # loop over frames from the video file stream
        var img = Mat()
        val url = "/Users/davidlopez.dayer/SampleOpenCV/src/video_out.mp4"
        val fps = capture[Videoio.CAP_PROP_FPS]
        val size = Size(capture[Videoio.CAP_PROP_FRAME_WIDTH], capture[Videoio.CAP_PROP_FRAME_HEIGHT])
        val writer = VideoWriter(url,
            VideoWriter.fourcc('m', 'p', '4', 'v'),
            fps, size, true
        )

        var i = 0
        while (i < 50) {
            capture.read(img)
            if (img.empty()) break
            drawBoxesOnTheImage(img)
            writer.write(img)
            i += 1
        } // of main while()

        capture.release()
        writer.release()

        println("----Finished----")

    }

    fun playVideo3(stage: Stage?) {
        val path = "/Users/davidlopez.dayer/SampleOpenCV/src/video_out.mp4"

        //Instantiating Media class

        //Instantiating Media class
        val media = Media(File(path).toURI().toString())

        //Instantiating MediaPlayer class

        //Instantiating MediaPlayer class
        val mediaPlayer = MediaPlayer(media)

        //Instantiating MediaView class

        //Instantiating MediaView class
        val mediaView = MediaView(mediaPlayer)

        //by setting this property to true, the Video will be played

        //by setting this property to true, the Video will be played
        mediaPlayer.isAutoPlay = true

        //setting group and scene

        //setting group and scene
        val root = Group()
        root.children.add(mediaView)
        val scene = Scene(root, 500.0, 1400.0)

        stage?.apply {
            setScene(scene)
            title = "Playing video"
            show()
        }
    }

    private fun drawBoxesOnTheImage(img: Mat){
        val gray = Mat()
        Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY)
        Imgproc.blur(gray, gray, Size(3.0, 3.0))

        val edges = Mat()
        val lowThreshold = 40
        val ratio = 3
        Imgproc.Canny(gray, edges, lowThreshold.toDouble(), (lowThreshold * ratio).toDouble())

        val circles = Mat()

        Imgproc.HoughCircles(edges, circles, Imgproc.CV_HOUGH_GRADIENT, 1.0, 60.0, 200.0, 20.0, 30, 0)

        //println("#rows " + circles.rows() + " #cols " + circles.cols())
        var x = 0.0
        var y = 0.0
        var r = 0

        for (i in 0 until circles.rows()) {
            val data = circles[i, 0]
            for (j in data.indices) {
                x = data[0]
                y = data[1]
                r = data[2].toInt()
            }
            val center = Point(x, y)
            // circle center
            Imgproc.circle(img, center, 3, Scalar(0.0, 255.0, 0.0), -1)
            // circle outline
            Imgproc.circle(img, center, r, Scalar(0.0, 0.0, 255.0), 1)
        }
    }

    override fun start(p0: Stage?) {
        playVideo3(p0)
    }

}