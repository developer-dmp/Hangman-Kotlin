package com.dmp.hangman

import org.apache.commons.mail.DefaultAuthenticator
import org.apache.commons.mail.HtmlEmail
import java.net.URL

class Email {

    private var senderEmail : String
    private var password : String
    private var message  : String

    constructor(senderEmail: String, password: String, message: String) {
        this.senderEmail = senderEmail
        this.password = password
        this.message = message
    }

    fun generateEmail() {
        val email = HtmlEmail()
        email.hostName = "smtp.googlemail.com"
        email.setSmtpPort(465)
        email.setAuthenticator(DefaultAuthenticator(senderEmail, password))
        email.isSSLOnConnect = true
        email.setFrom(senderEmail)
        email.addTo("dmp001@gmail.com")
        email.subject = message
        //val kotlinLogoURL = URL("https://kotlinlang.org/assets/images/twitter-card/kotlin_800x320.png")
        //val cid = email.embed(kotlinLogoURL, "Kotlin logo")
        //email.setHtmlMsg("<html><h1>Kotlin logo</h1><img src=\"cid:$cid\"></html>")
        email.send()
    }
}