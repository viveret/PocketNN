package com.viveret.pocketn2.view.dialog

import android.content.pm.PackageInfo
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.viveret.pocketn2.R
import com.viveret.pocketn2.databinding.FragmentWhatsNewBinding
import org.jetbrains.anko.contentView
import org.jetbrains.anko.defaultSharedPreferences
import ru.noties.markwon.Markwon
import java.text.SimpleDateFormat
import java.util.*

class WhatsNewDialog(val activity: AppCompatActivity) {
    private val numberOfChangesToShow = 6
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val shouldBeShown: Boolean
    private val packageInfo: PackageInfo = activity.packageManager.getPackageInfo(activity.packageName, 0)!!
    private val changelogIds = arrayOf(
            //R.string.version_17_1_12_summary,
            R.string.version_16_1_11_summary,
            R.string.version_15_1_10a_summary,
            R.string.version_14_1_10_summary,
            R.string.version_13_1_9_summary,
            R.string.version_12_1_8_summary,
            R.string.version_11_1_7_summary,
            R.string.version_10_1_6_summary,
            R.string.version_9_1_5_summary,
            R.string.version_8_1_4b_summary,
            R.string.version_7_1_4a_summary,
            R.string.version_6_1_4_summary,
            R.string.version_5_1_3a_summary,
            R.string.version_4_1_3_summary,
            R.string.version_3_1_2_summary,
            R.string.version_2_1_1_summary,
            R.string.version_1_1_0_summary)

    init {
        val sharedPrefs = activity.defaultSharedPreferences
        this.shouldBeShown = !sharedPrefs.contains("ignore-whats-new") && sharedPrefs.getInt("version", -1) < packageInfo.versionCode
    }

    fun show() {
        val binding = FragmentWhatsNewBinding.inflate(activity.layoutInflater, activity.contentView as ViewGroup, false)
        val whatsNewView = binding.root
        val whatsNewList = binding.list

        val markdownString = changelogIds.take(numberOfChangesToShow).joinToString("\n\n") { x -> Changeset(activity.getString(x), dateFormat).toString() }

        val markwon = Markwon.create(activity)
        markwon.setMarkdown(whatsNewList, markdownString)

        val builder = AlertDialog.Builder(activity)
        builder.setView(whatsNewView).setTitle("What's New")
                .setPositiveButton("AWESOME") { dialog, _ -> acknowledgeMostRecentUpdate(); dialog.dismiss() }
                .setNegativeButton("I DON'T CARE") { dialog, _ -> ignoreFutureUpdates(); dialog.dismiss()}
        builder.create().show()
    }

    fun acknowledgeMostRecentUpdate() {
        activity.defaultSharedPreferences.edit().putInt("version", packageInfo.versionCode).apply()
    }

    fun ignoreFutureUpdates() {
        activity.defaultSharedPreferences.edit().putBoolean("ignore-whats-new", true).apply()
    }

    class Changeset(input: String, dateFormat: SimpleDateFormat) {
        val date: Date
        val versionName: String
        val versionCode: Int
        val message: String

        init {
            val reader =  Scanner(input)
            reader.useDelimiter("\n")
            var numFieldsUnresolved = 4
            var date = Date(Long.MIN_VALUE)
            var versionName = ""
            var versionCode = 0
            while (numFieldsUnresolved > 0 && reader.hasNextLine()) {
                val line = reader.next().trim()
                if (line.isBlank()) continue
                val split = line.split(":")
                when (split.first()) {
                    "version" -> versionName = split.last()
                    "versionCode" -> versionCode = split.last().toInt()
                    "date" -> date = dateFormat.parse(split.last())!!
                    "message" -> numFieldsUnresolved = 0
                }
            }
            this.versionName = versionName
            this.versionCode = versionCode
            this.date = date
            val msgSb = StringBuilder()
            while (reader.hasNextLine()) {
                msgSb.append(reader.nextLine())
                msgSb.append("\n")
            }
            this.message = msgSb.toString()
        }

        override fun toString(): String = "## ${date.toLocaleString().removeSuffix(" 00:00:00")} - $versionName\n$message"
    }
}