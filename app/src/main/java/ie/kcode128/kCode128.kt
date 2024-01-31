package ie.kcode128

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import kotlin.experimental.xor


class kCode128 : AppCompatImageView {

    // Whole docs and info you can get here
    // https://en.wikipedia.org/wiki/Code_128

    private val CODE_START = 104
    private val CODE_STOP = 106
    private val DIVISOR = 103

    private val BARCODE_COLOR = Color.BLACK
    private val BARCODE_BG_COLOR = Color.WHITE

    private var data: String? = null
    private var weight = 0
    private var weight_sum = 0
    private var check_sum = 0

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setupBarCodeView(w, h)
    }

    private fun setupBarCodeView(w: Int, h: Int) {
        resetValues()
        setImageBitmap(getBitmap(w, h))
    }

    private fun resetValues() {
        weight = 0
        weight_sum = 0
        check_sum = 0
    }

    fun setData(data: String) {
        this.data = data
    }

    fun initBuffer(dataLen: Int): ByteArray {
        var sum = 0
        //add start code 11byte
        sum += 11
        //add encoded data 11 byte * dataLen
        sum += dataLen * 11
        //add check sum
        sum += 11
        //add end code 12byte
        sum += 13
        // sum = 11 + 11 + 12 + (11*dataLen);
        return ByteArray(sum)
    }

    fun encode(): ByteArray {
        if (data == null) {
            return ByteArray(0)
        }

        val len = data!!.length
        val buffer = initBuffer(len)
        var pos = 0

        var count = appendData(CODE_WEIGHT[CODE_START], buffer, pos)
        pos += count
        weight_sum = CODE_START
        for (i in 0 until len) {
            weight++
            val ch = data!![i]
            val index = ch - ' '
            val ch_weight = CODE_WEIGHT[index]
            count = appendData(ch_weight, buffer, pos)
            pos += count
            val weightByValue = weight * index
            weight_sum += weightByValue
        }

        check_sum = weight_sum % DIVISOR

        count = appendData(CODE_WEIGHT[check_sum], buffer, pos)
        pos += count
        count = appendData(CODE_WEIGHT[CODE_STOP], buffer, pos)
        pos += count

        return buffer
    }

    private fun getBitmap(w: Int, h: Int): Bitmap {
        val code = encode()

        val resources = context.resources
        val scale = resources.displayMetrics.density

        val TOP_GAP = 30
        val BOTTOM_GAP = if (resources.displayMetrics.densityDpi <= 240) 60 else 100
        val inputWidth = code.size

        // Add quiet zone on both sides
        val fullWidth = inputWidth + 6
        val outputWidth = Math.max(w, fullWidth)
        val outputHeight = Math.max(1, h) - BOTTOM_GAP
        val multiple = outputWidth / fullWidth
        val leftPadding = (outputWidth - inputWidth * multiple) / 2

        //BitMatrix output = new BitMatrix(outputWidth, outputHeight);
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        // Whole background area Colour
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        bgPaint.color = BARCODE_BG_COLOR

        val bounds = Rect(0, 0, w, h)
        canvas.drawRect(bounds, bgPaint)

        val barPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        barPaint.color = BARCODE_COLOR
        barPaint.strokeWidth = 0F

        var inputX = 0
        var outputX = leftPadding
        while (inputX < inputWidth) {
            if (code.get(inputX).toInt() == 1) {
                canvas.drawRect(
                    outputX.toFloat(),
                    TOP_GAP.toFloat(),
                    (outputX + multiple).toFloat(),
                    outputHeight.toFloat(),
                    barPaint
                )
            }
            inputX++
            outputX += multiple
        }
        // Colour of BarCode
        bgPaint.color = BARCODE_COLOR
        val size = (18 * scale)
        bgPaint.textSize = size
        val str = insertSpace(data.toString())
        bgPaint.textAlign = Paint.Align.CENTER
        canvas.drawText(str, (w / 2).toFloat(), (h - TOP_GAP).toFloat(), bgPaint)
        return bitmap
    }

    private fun insertSpace(data: String): String {
        val sb = StringBuilder()
        var cnt = 1
        val len = data.length
        var i = 0
        while (i < len) {
            sb.append(data[i])
            if (cnt % 4 == 0) {
                sb.append(" ")
            }
            i++
            cnt++
        }
        return sb.toString()
    }

    private fun appendData(weights: ByteArray, dst: ByteArray, pos: Int): Int {
        var color: Byte = 1
        var count = 0
        var index = pos

        for (weight in weights) {
            for (i in 0 until weight) {
                dst[index] = color
                index++
                count++
            }
            color = color xor 1
        }

        return count
    }

    // Check https://en.wikipedia.org/wiki/Code_128 for more details
    private val CODE_WEIGHT = arrayOf(
        byteArrayOf(2, 1, 2, 2, 2, 2), // 0
        byteArrayOf(2, 2, 2, 1, 2, 2),
        byteArrayOf(2, 2, 2, 2, 2, 1),
        byteArrayOf(1, 2, 1, 2, 2, 3),
        byteArrayOf(1, 2, 1, 3, 2, 2),
        byteArrayOf(1, 3, 1, 2, 2, 2), // 5
        byteArrayOf(1, 2, 2, 2, 1, 3),
        byteArrayOf(1, 2, 2, 3, 1, 2),
        byteArrayOf(1, 3, 2, 2, 1, 2),
        byteArrayOf(2, 2, 1, 2, 1, 3),
        byteArrayOf(2, 2, 1, 3, 1, 2), // 10
        byteArrayOf(2, 3, 1, 2, 1, 2),
        byteArrayOf(1, 1, 2, 2, 3, 2),
        byteArrayOf(1, 2, 2, 1, 3, 2),
        byteArrayOf(1, 2, 2, 2, 3, 1),
        byteArrayOf(1, 1, 3, 2, 2, 2), // 15
        byteArrayOf(1, 2, 3, 1, 2, 2),
        byteArrayOf(1, 2, 3, 2, 2, 1),
        byteArrayOf(2, 2, 3, 2, 1, 1),
        byteArrayOf(2, 2, 1, 1, 3, 2),
        byteArrayOf(2, 2, 1, 2, 3, 1), // 20
        byteArrayOf(2, 1, 3, 2, 1, 2),
        byteArrayOf(2, 2, 3, 1, 1, 2),
        byteArrayOf(3, 1, 2, 1, 3, 1),
        byteArrayOf(3, 1, 1, 2, 2, 2),
        byteArrayOf(3, 2, 1, 1, 2, 2), // 25
        byteArrayOf(3, 2, 1, 2, 2, 1),
        byteArrayOf(3, 1, 2, 2, 1, 2),
        byteArrayOf(3, 2, 2, 1, 1, 2),
        byteArrayOf(3, 2, 2, 2, 1, 1),
        byteArrayOf(2, 1, 2, 1, 2, 3), // 30
        byteArrayOf(2, 1, 2, 3, 2, 1),
        byteArrayOf(2, 3, 2, 1, 2, 1),
        byteArrayOf(1, 1, 1, 3, 2, 3),
        byteArrayOf(1, 3, 1, 1, 2, 3),
        byteArrayOf(1, 3, 1, 3, 2, 1), // 35
        byteArrayOf(1, 1, 2, 3, 1, 3),
        byteArrayOf(1, 3, 2, 1, 1, 3),
        byteArrayOf(1, 3, 2, 3, 1, 1),
        byteArrayOf(2, 1, 1, 3, 1, 3),
        byteArrayOf(2, 3, 1, 1, 1, 3), // 40
        byteArrayOf(2, 3, 1, 3, 1, 1),
        byteArrayOf(1, 1, 2, 1, 3, 3),
        byteArrayOf(1, 1, 2, 3, 3, 1),
        byteArrayOf(1, 3, 2, 1, 3, 1),
        byteArrayOf(1, 1, 3, 1, 2, 3), // 45
        byteArrayOf(1, 1, 3, 3, 2, 1),
        byteArrayOf(1, 3, 3, 1, 2, 1),
        byteArrayOf(3, 1, 3, 1, 2, 1),
        byteArrayOf(2, 1, 1, 3, 3, 1),
        byteArrayOf(2, 3, 1, 1, 3, 1), // 50
        byteArrayOf(2, 1, 3, 1, 1, 3),
        byteArrayOf(2, 1, 3, 3, 1, 1),
        byteArrayOf(2, 1, 3, 1, 3, 1),
        byteArrayOf(3, 1, 1, 1, 2, 3),
        byteArrayOf(3, 1, 1, 3, 2, 1), // 55
        byteArrayOf(3, 3, 1, 1, 2, 1),
        byteArrayOf(3, 1, 2, 1, 1, 3),
        byteArrayOf(3, 1, 2, 3, 1, 1),
        byteArrayOf(3, 3, 2, 1, 1, 1),
        byteArrayOf(3, 1, 4, 1, 1, 1), // 60
        byteArrayOf(2, 2, 1, 4, 1, 1),
        byteArrayOf(4, 3, 1, 1, 1, 1),
        byteArrayOf(1, 1, 1, 2, 2, 4),
        byteArrayOf(1, 1, 1, 4, 2, 2),
        byteArrayOf(1, 2, 1, 1, 2, 4), // 65
        byteArrayOf(1, 2, 1, 4, 2, 1),
        byteArrayOf(1, 4, 1, 1, 2, 2),
        byteArrayOf(1, 4, 1, 2, 2, 1),
        byteArrayOf(1, 1, 2, 2, 1, 4),
        byteArrayOf(1, 1, 2, 4, 1, 2), // 70
        byteArrayOf(1, 2, 2, 1, 1, 4),
        byteArrayOf(1, 2, 2, 4, 1, 1),
        byteArrayOf(1, 4, 2, 1, 1, 2),
        byteArrayOf(1, 4, 2, 2, 1, 1),
        byteArrayOf(2, 4, 1, 2, 1, 1), // 75
        byteArrayOf(2, 2, 1, 1, 1, 4),
        byteArrayOf(4, 1, 3, 1, 1, 1),
        byteArrayOf(2, 4, 1, 1, 1, 2),
        byteArrayOf(1, 3, 4, 1, 1, 1),
        byteArrayOf(1, 1, 1, 2, 4, 2), // 80
        byteArrayOf(1, 2, 1, 1, 4, 2),
        byteArrayOf(1, 2, 1, 2, 4, 1),
        byteArrayOf(1, 1, 4, 2, 1, 2),
        byteArrayOf(1, 2, 4, 1, 1, 2),
        byteArrayOf(1, 2, 4, 2, 1, 1), // 85
        byteArrayOf(4, 1, 1, 2, 1, 2),
        byteArrayOf(4, 2, 1, 1, 1, 2),
        byteArrayOf(4, 2, 1, 2, 1, 1),
        byteArrayOf(2, 1, 2, 1, 4, 1),
        byteArrayOf(2, 1, 4, 1, 2, 1), // 90
        byteArrayOf(4, 1, 2, 1, 2, 1),
        byteArrayOf(1, 1, 1, 1, 4, 3),
        byteArrayOf(1, 1, 1, 3, 4, 1),
        byteArrayOf(1, 3, 1, 1, 4, 1),
        byteArrayOf(1, 1, 4, 1, 1, 3), // 95
        byteArrayOf(1, 1, 4, 3, 1, 1),
        byteArrayOf(4, 1, 1, 1, 1, 3),
        byteArrayOf(4, 1, 1, 3, 1, 1),
        byteArrayOf(1, 1, 3, 1, 4, 1),
        byteArrayOf(1, 1, 4, 1, 3, 1), // 100
        byteArrayOf(3, 1, 1, 1, 4, 1),
        byteArrayOf(4, 1, 1, 1, 3, 1),
        byteArrayOf(2, 1, 1, 4, 1, 2),
        byteArrayOf(2, 1, 1, 2, 1, 4),
        byteArrayOf(2, 1, 1, 2, 3, 2), // 105
        byteArrayOf(2, 3, 3, 1, 1, 1, 2)
    )
}

