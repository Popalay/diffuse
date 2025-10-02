package com.jakewharton.diffuse.report.json

import com.jakewharton.diffuse.diffuseTable
import com.jakewharton.diffuse.format.Apk
import com.jakewharton.diffuse.format.ArchiveFile
import com.jakewharton.diffuse.format.ArchiveFiles
import com.jakewharton.diffuse.info.toSummaryTable
import com.jakewharton.diffuse.io.Size
import com.jakewharton.diffuse.report.Report
import com.jakewharton.diffuse.report.toSummaryString
import com.jakewharton.picnic.TableSectionDsl
import com.jakewharton.picnic.TextAlignment
import com.jakewharton.picnic.renderText
import kotlin.collections.filterValues
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.putJsonObject

private class ApkInfoJsonModel(
  val filename: String,
  val signatures: String,
  val files: String,
  val dexes: String,
  val arsc: String,
)

class ApkInfoJsonReport(
  private val apk: Apk,
) : Report {
  override fun write(appendable: Appendable) {
    appendable.apply {

    }

    appendable.apply {
      append(apk.filename)
      append(" (signature: ")
      append(apk.signatures.toSummaryString())
      appendLine(')')
      appendLine()

      appendLine(
        apk.files.toSummaryTable(
          "APK",
          ArchiveFile.Type.APK_TYPES,
          skipIfEmptyTypes = setOf(ArchiveFile.Type.Native),
        ),
      )

//      appendLine()
//      appendLine(apk.dexes.toSummaryTable())
//      appendLine()
//      appendLine(apk.arsc.toSummaryTable())
    }
  }

  override fun toString() = apk.files.toSummaryJson(
    ArchiveFile.Type.APK_TYPES,
    skipIfEmptyTypes = setOf(ArchiveFile.Type.Native),
  ).toString()
}


internal fun ArchiveFiles.toSummaryJson(
  displayTypes: List<ArchiveFile.Type>,
  skipIfEmptyTypes: Set<ArchiveFile.Type> = emptySet(),
) = buildJsonObject {

  fun JsonObjectBuilder.addApkRow(name: String, type: ArchiveFile.Type? = null) {
    val old = if (type != null) filterValues { it.type == type } else this@toSummaryJson
    val oldSize = old.values.fold(Size.ZERO) { acc, file -> acc + file.size }
    val oldUncompressedSize =
      old.values.fold(Size.ZERO) { acc, file -> acc + file.uncompressedSize }
    if (oldSize != Size.ZERO || type !in skipIfEmptyTypes) {
      putJsonObject(name) {
        put("size", JsonPrimitive(oldSize.bytes))
        put("uncompressed", JsonPrimitive(oldUncompressedSize.bytes))
      }
    }
  }

  for (type in displayTypes) {
    addApkRow(type.displayName, type)
  }

  addApkRow("total")
}
