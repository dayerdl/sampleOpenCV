import javafx.application.Application
import javafx.application.Platform
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.scene.media.MediaView
import javafx.stage.Stage
import org.opencv.core.*
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import org.opencv.videoio.VideoCapture
import org.opencv.videoio.VideoWriter
import org.opencv.videoio.Videoio
import java.io.File


object PesoMuerto : Application() {
    @JvmStatic
    fun main(args: Array<String>) {
        //Compulsory -> Load Native libraries
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
        println("Loaded OpenCV version " + Core.VERSION)
        Platform.runLater {
            start(Stage())
        }
    }

    private fun detectObjectInVideo(url: String): String {

        val capture = VideoCapture(url)

        var frmCount = 0.0
        if (capture.isOpened) {
            frmCount = capture.get(Videoio.CAP_PROP_FRAME_COUNT)
            println("frmCount = $frmCount")
            // CAP_PROP_FRAME_COUNT
        } else {
            println("Capture is not opened!")
            return ""
        }

        // # loop over frames from the video file stream
        var img = Mat()
        val fps = capture[Videoio.CAP_PROP_FPS]
        val size = Size(capture[Videoio.CAP_PROP_FRAME_WIDTH], capture[Videoio.CAP_PROP_FRAME_HEIGHT])
        val outputUrl = "/Users/davidlopez.dayer/SampleOpenCV/src/video_output.mp4"
        val writer = VideoWriter(outputUrl, VideoWriter.fourcc('m', 'p', '4', 'v'), fps, size, true)
        val dst = Mat()

        var i = 0
        while (i < 50) {
            capture.read(img)
            if (img.empty()) break
            drawBoxesOnTheImage(img)
            MatUtils.rotateImage(img, dst)
            writer.write(dst)
            i += 1
            println("writing $i")
        } // of main while()

        capture.release()
        writer.release()

        println("----Finished----")
        return outputUrl

    }

    fun playVideo(path: String, stage: Stage?) {
        val media = Media(File(path).toURI().toString())
        val mediaPlayer = MediaPlayer(media)
        val mediaView = MediaView(mediaPlayer)
        mediaPlayer.isAutoPlay = true
        val root = Group()
        root.children.add(mediaView)
        val scene = Scene(root, 500.0, 1400.0)

        stage?.apply {
            setScene(scene)
            title = "Playing video"
            show()
        }
    }

    private fun drawBoxesOnTheImage(img: Mat) {
        val gray = Mat()
        Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY)
        Imgproc.blur(gray, gray, Size(3.0, 3.0))

        val edges = Mat()
        val lowThreshold = 40
        val ratio = 3
        Imgproc.Canny(gray, edges, lowThreshold.toDouble(), (lowThreshold * ratio).toDouble())

        val circles = Mat()

        Imgproc.HoughCircles(edges, circles, Imgproc.CV_HOUGH_GRADIENT, 1.0, 60.0, 200.0, 20.0, 30, 100)

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
            // circle outline
            Imgproc.circle(img, center, r, Scalar(0.0, 0.0, 255.0), 1)
        }
    }

    override fun start(p0: Stage?) {
        val inputUrl = "/Users/davidlopez.dayer/SampleOpenCV/src/peso-muerto.mp4"
        val outputUrl = detectObjectInVideo(inputUrl)
        playVideo(outputUrl, p0)
    }

}