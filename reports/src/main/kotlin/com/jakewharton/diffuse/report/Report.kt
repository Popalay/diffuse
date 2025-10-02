package com.jakewharton.diffuse.report

interface Report {
  fun write(appendable: Appendable)

  interface Factory {
    fun toTextReport(): Report
    fun toHtmlReport(): Report {
      TODO("Implement HTML reporting")
    }
    fun toJsonReport(): Report {
      TODO("Implement JSON reporting")
    }
  }
}
