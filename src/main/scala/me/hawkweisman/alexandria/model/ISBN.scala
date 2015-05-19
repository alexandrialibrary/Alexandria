package me.hawkweisman.alexandria
package model

import scala.util.{ Try, Success, Failure }

case class ISBN(self: Long) {
  /**
   * Format the ISBN as a [[String]] suitable for printing
   * @return the ISBN formatted as a [[String]]
   */
  def format: String = ???

  /**
   * Attempt to look up the book metadata for this ISBN.
   * Book metadata comes from the Google Books API.
   * @return [[Success]] containing a [[Book]] if a book was found for this ISBN,
   */
  def lookup: Try[Book] = ???

}
object ISBN {

  val ISBN13 = ("""^?:ISBN(?:-13)?:?\ )?""" + // Optional ISBN/ISBN13 identifier
                """(?=""" +                   // Basic format pre-checks:
                """[0-9]{13}""" +             // - must be 13 digits
                """|""" +                     //  OR
                """(?=(?:[0-9]+[-\ ]){4})""" +// - must have 4 separator characters
                """[-\ 0-9]{17}""" +          // - out of 17 characters total
                """)""" +                     // End format pre-checks
                """97[89][-\ ]?""" +          // ISBN-13 prefix code
                """([0-9]{1,5})[-\ ]?""" +    // Capture group 1: group ID
                """([0-9]+)[-\ ]?""" +        // Capture group 2: publisher ID
                """([0-9]+)[-\ ]?""" +        // Capture group 3: title ID
                """([0-9])""" +               // Capture group 4: Check digit.
                """$""").r

  val ISBN10 = ("""^(?:ISBN(?:-10)?:?\ )?""" +// Optional ISBN/ISBN-10 identifier.
                """(?=""" +                   // Basic format pre-checks:
                """[0-9X]{10}$""" +           // - must be 10 digits/Xs (no separators).
                """|""" +                     //  OR
                """(?=(?:[0-9]+[-\ ]){3})""" +// - must have 3 separator characters
                """[-\ 0-9X]{13}$""" +        // - out of 13 characters total
                """)""" +                     // End format pre-checks
                """([0-9]{1,5})[-\ ]?""" +    // Capture group 1: group ID
                """([0-9]+)[-\ ]?""" +        // Capture group 2: publisher ID
                """([0-9]+)[-\ ]?""" +        // Capture group 3: title ID
                """([0-9X])""" +              // Capture group 4: Check digit.
                """$""").r

  /**
   * Parse a [[String]] to an [[ISBN]]
   * @return Either a [[Success]] containing an ISBN or a [[Failure]] if the string could not be parsed.
   */
  def parse(str: String): Try[ISBN] = str match {
    case ISBN13(group,publisher,title,check) => ???
    case ISBN10(group,publisher,title,check) => ???
    case _ => Failure(new NumberFormatException(s"$str was not a valid ISBN number"))
  }

}
