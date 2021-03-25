tasks.withType<JacocoReport> {
    reports {
        csv.isEnabled = false
        html.isEnabled = true
        xml.isEnabled = true
    }

    dependsOn("test")
}
