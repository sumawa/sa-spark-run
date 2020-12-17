//package com.sa.sparkrun
//
//object PalindromeExtractor extends App{
//  def palindromes(input: String): Seq[String] = {
//    val in = input.replaceAll("[^A-Za-z0-9]", "").toLowerCase
//    for {
//      i <- 2 to in.length
//      step = i / 2
//      s <- in.sliding(i) if s == s.reverse
//    } yield s
//  }
//  println(palindromes("Aasdfsdfsfwewfwlkn"))
//  println(" --- ")
////    println(palindromes("Aasdfsdfsfwewfwlkn"))
//}