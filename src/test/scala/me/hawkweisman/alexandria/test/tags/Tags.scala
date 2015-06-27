package me.hawkweisman.alexandria.test
package tags

import org.scalatest.Tag

/**
 * Tag for tests that require a running database.
 * Created by hawk on 6/27/15.
 */
object DbTest extends Tag("me.hawkweisman.alexandria.test.tags.DbTest")

/**
 * Tags for tests that require an internet connection.
 * Created by hawk on 6/27/15.
 */
object InternetTest extends Tag("me.hawkweisman.alexandria.test.tags.InternetTest")
