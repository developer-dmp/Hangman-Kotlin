package com.dmp.hangman

import java.io.File
import java.io.InputStream
import java.util.*

// class-level variables for the game
val LOADING_DELAY : Long = 250
var remainingLives : Int = 0
var guessedCharacterList = mutableListOf<Char>()
var wordToGuess : String? = null
var userName : String? = null

var playing = true
var won = false

val ANSI_RESET = "\u001B[0m"
val ANSI_RED = "\u001B[31m"
val ANSI_BLUE = "\u001B[34m"
val ANSI_CYAN = "\u001B[36m"
val ANSI_GREEN = "\u001B[32m"
val ANSI_YELLOW = "\u001B[33m"
val ANSI_PURPLE = "\u001B[35m"

/**
 * Basic hangman game, in Kotlin
 *
 * @author Domenic Polidoro
 * @version 1.0
 */
fun main(args: Array<String>) {

    // gather user name
    obtainUserName()

    // method so we can call it anywhere and restart the game
    initializeGame()

    // game has completely ended
    println("\nHave a nice day :)")
}

/**
 * Helper method to run the game.  Can be called the
 * first time the game is ran or any time after that.
 */
fun initializeGame() {

    // reinitialize variables (useful in the case the user restarts game at end)
    playing = true
    wordToGuess = ""
    remainingLives = 0
    guessedCharacterList.clear()

    // gather potential words
    readWordFile()

    // gather user difficulty
    obtainDifficulty()

    // play game!
    playGame()
}

/**
 * Kick off the looping logic to continue asking
 * the user for guesses and handling their input
 * until they either win or lose the game.
 */
fun playGame() {

    while (playing) {
        //printSeparator(true, true, '*')
        printWord()
        printInfo()
        //printSeparator(false, true, '*')
        promptGuess()
        printSeparator(true, true, '#', 25)
        checkGameOver()
    }

    // since the game has ended for some reason, show the user a message
    if (won) {
        printWord()
        printWinnerMessage()
    } else {
        printLoserMessage()
    }
}

fun printWinnerMessage() {
    println(ANSI_CYAN + "You won!" + ANSI_RESET)
    val email = Email(
            "developer.dmp@gmail.com",
            "shoot4thestars",
            "Hello $userName,\n\nYou won Kotlin-Hangman!!")
    email.generateEmail()
}

/**
 * The user has lost the game, notify them and prompt
 * for them to play again.
 */
fun printLoserMessage() {
    print(ANSI_RED + "You lost!" +
            "\n\nThe word you were looking for was: [$wordToGuess]" +
            "\n\nWould you like to play again? (y/n): " + ANSI_RESET)

    // wait for user response, act if necessary
    if (readLine() == "y") {
        println("Good choice, $userName!  Best of luck this time.")
        initializeGame()
    }
}

/**
 * Method to determine if the user has guessed the
 * entire word.
 */
fun checkGameOver() {
    var complete = true
    wordToGuess?.forEach {
        c -> if (!guessedCharacterList.contains(c)) {
            complete = false
        }
    }

    // they have won
    if (complete) {
        playing = false
        won = true
    }
}

/**
 * Method to capture the user's input and process
 * their guessed letter.
 */
fun promptGuess() {
    var letter : Char
    // loop until we have valid input, then proceed
    do {
        print(ANSI_BLUE+"> "+ ANSI_RESET)

        // capture user input
        val input = readLine()!!.toLowerCase()

        // validate it
        letter = when {
            input.length == 1 -> input.single()
            else -> '='
        }
    } while (!letter.isLetter())

    // process guessed letter
    if (guessedCharacterList.contains(letter)) {
        // todo captured duplicate guess
    } else {
        // add letter to the list of guess characters
        guessedCharacterList.add(letter)

        // check to see if the letter was in the word
        if (!wordToGuess!!.contains(letter)) {
            remainingLives--
        }
    }

    // if we are out of lives, the game is over
    if (remainingLives == 0) {
        playing = false
        won = false
    }
}

/**
 * Helper to print information about how many lives
 * the user has left and what letters they have
 * already guessed.
 */
fun printInfo() {
    when {
        remainingLives <= 3 -> print(ANSI_RED)
        remainingLives <= 6 -> print(ANSI_YELLOW)
        else                -> print(ANSI_GREEN)
    }
    println("LIVES: $remainingLives" + ANSI_RESET)
    print("GUESSED: ")
    guessedCharacterList.forEach {
        c -> print(c+" ")
    }
    println()
}

/**
 * Method to either print an underscore for each
 * letter in the word the user hasn't guessed, or
 * fill in the letters the user has guessed.
 */
fun printWord() {

    wordToGuess?.forEach {
        c -> print(determineHit(c)+" ")
    }
    println()
}

/**
 * Helper to determine if the user has guessed each
 * character in the string.
 */
fun determineHit(c: Char) : Char {
    return when (guessedCharacterList.contains(c)) {
        true -> c
        false -> '_'
    }
}

/**
 * Method to prompt the user with a difficulty menu
 * and capture their response.
 */
fun obtainDifficulty() {

    var diff: String

    // loop until we get valid input, then proceed
    do {
        print("\nSelect Difficulty" +
                ANSI_GREEN  + "\n1. Easy" + ANSI_RESET +
                ANSI_YELLOW + "\n2. Medium" + ANSI_RESET +
                ANSI_RED    + "\n3. Hard" + ANSI_RESET)
        print(ANSI_BLUE+"\n> "+ ANSI_RESET)

        diff = readLine()!!.trim()
        if (diff == "1") {
            remainingLives = 10
            break
        } else if (diff == "2") {
            remainingLives = 8
            break
        } else if (diff == "3") {
            remainingLives = 6
            break
        }
    } while (true)
}

/**
 * Method to loop until we get a valid user's name
 * and prompt them with a welcome message asking if
 * they know the rules already.
 */
fun obtainUserName() {
    var name: String
    do {
        print("Enter your name: ")
        name = readLine()!!.trim()
    } while (name.isEmpty())

    userName = name.toUpperCase()
    print("Welcome to Kotlin-Hangman, $userName!\n")

    // check to see if we need to display the rules to the user
    showRules()
}

/**
 * Helper method to print out the rules to the user
 */
fun showRules() {
    println(ANSI_BLUE +
            "\n********** RULES **********"+
            "\n- Select a difficulty" +
            "\n- Guess a letter" +
            "\n- If it's wrong, you lose a life" +
            "\n- If it's right, you'll see the letter appear" +
            "\n- Reveal the whole word before you run out of lives" +
    ANSI_RESET)
}

/**
 * Method to parse out the file containing all the words and
 * randomly select one that the user will attempt to discover.
 * Prints progress of reading the file to the screen
 */
fun readWordFile() {
    println("\nLoading ...")
    val inputStream: InputStream = File("assets\\words.txt").inputStream()
    val wordList = mutableListOf<String>()
    var openBracket = true
    var count = 0

    // add each line to the list and print progress to console
    inputStream.bufferedReader().useLines {
        lines -> lines.forEach {
            wordList.add(it)
            if (openBracket) {
                print("<")
            } else {
                print(">")
            }
            openBracket = !openBracket
            count++
            Thread.sleep(LOADING_DELAY)
        }
    }
    // randomly select a word in the list
    wordToGuess = wordList[Random().nextInt(count)]
    print("\n")
}

/**
 * Helper method to print the specific character passed
 * into the method 'n' times.
 *
 * @param before - boolean to see if we print a newline before characters
 * @param after - boolean to see if we print a newline after characters
 * @param char - the character to print to the terminal
 * @param n - the number of times the character should be printed
 */
fun printSeparator(before: Boolean, after: Boolean, char: Char, n: Int) {
    if (before) {
        print("\n")
    }

    var line = ""
    for (i in 1..n) {
        line+=char
    }

    print(ANSI_PURPLE + line + ANSI_RESET)

    if (after) {
        print("\n")
    }
}