# Alexandria
Alexandria is a simple little card catalogue webapp with a terribly pretentious name.

## About Alexandria
### Who is it for?
Alexandria is intended primarily for managing relatively small libraries, such as a particularly large home library or a library within a department or organization. Therefore, it is intended to be simple to set up and easy to use. There's no particular reason why Alexandria can't be used by much larger libraries, as well; however, larger users may want a more complex solution with more features.

### How does it work?
Alexandria is written in the [Scala](http://www.scala-lang.org) programming language, using the [Scalatra](http://www.scalatra.org) web framework. Web views are themed using [Bootstrap](http://getbootstrap.com), because I'm lazy and bad at design. ISBN lookups are performed using the Google Books API.

## Using Alexandria
### Build and Run

```sh
$ cd alexandria
$ ./sbt
> container:start
> browse
```

If `browse` doesn't launch your browser, manually open [http://localhost:8080/](http://localhost:8080/) in your browser.
