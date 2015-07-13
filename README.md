# Alexandria

[![Join the chat at https://gitter.im/alexandrialibrary/Alexandria](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/alexandrialibrary/Alexandria?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

[![Build Status](https://travis-ci.org/alexandrialibrary/Alexandria.svg)](https://travis-ci.org/alexandrialibrary/Alexandria)
[![Codacy Badge](https://www.codacy.com/project/badge/4d04c3b524bd49cd869595af2ffbb9eb)](https://www.codacy.com/app/alexandria/Alexandria)
[![Coverage](https://img.shields.io/codecov/c/github/alexandrialibrary/Alexandria/master.svg)]()
[![License](http://img.shields.io/:license-mit-blue.svg?style=flat)](http://doge.mit-license.org)
[![GitHub release](https://img.shields.io/github/tag/alexandrialibrary/Alexandria.svg)]()

Alexandria is a simple little card catalogue webapp with a terribly pretentious name.

## About Alexandria
### Who is it for?
Alexandria is intended primarily for managing relatively small libraries, such as a particularly large home library or a library within a department or organization. Therefore, it is intended to be simple to set up and easy to use. There's no particular reason why Alexandria can't be used by much larger libraries, as well; however, larger users may want a more complex solution with more features.

### How does it work?
Alexandria is written in the [Scala](http://www.scala-lang.org) programming language, using the [Scalatra](http://www.scalatra.org) web framework. Web views are themed using [Bootstrap](http://getbootstrap.com), because I'm lazy and bad at design. ISBN lookups are performed using the [OpenLibrary](http://openlibrary.org/) API.

### Goals

Alexandria should...
  + ...be simple and easy to use
  + ...require minimal configuration to start using
  + ...be as [un-astonishing](http://en.wikipedia.org/wiki/Principle_of_least_astonishment) as possible
  + ...allow the user to reconfigure everything if they want to
  + ...run acceptably on a Raspberry Pi (under low loads)

## Using Alexandria
### Build and Run

```sh
$ cd alexandria
$ ./sbt
> container:start
> browse
```

If `browse` doesn't launch your browser, manually open [http://localhost:8080/](http://localhost:8080/) in your browser.
