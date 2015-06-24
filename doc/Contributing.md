Contributing to Alexandria
==========================

Alexandria is open-source software licensed under the [MIT license](https://github.com/alexandrialibrary/Alexandria/blob/master/LICENSE). Therefore, contributions from any individuals interested in improving Alexandria will be accepted by the maintainers, provided that they follow the Alexandria Best Practices and Coding Style guidelines.

How to Contribute
-----------------

#### Pull Requests

If you wish to contribute to Alexandria, please fork the Alexandria repository, make any changes you wish to contribute, and then create a [pull request](https://github.com/alexandrialibrary/Alexandria/pulls) to merge your changes.

Your pull request will be reviewed by the repository maintainers (@redbassett and @hawkw), who will determine whether or not it can be merged. In most cases, if your pull request is not approved, the maintainers will provide you with information on why your pull request was not merged and what you should do in order to make it merge-ready.

In order to ensure that this process is as efficient as possible, please ensure that your contributions conform to the Alexandria Coding Style and Best Practices (as described in the next section), and run the Alexandria test suite locally on your development machine to ensure that your contribution builds successfully. Furthermore, please try to provide a detailed explanation of what your pull request adds or fixes, how this is implemented, and why it is necessary. This will make the review process much easier for both the contributor (you) and the repository maintainers. Pull requests that are not adequately described will not be approved to merge.

If you are unsure how to create a pull request, please consult the [GitHub documentation](https://help.github.com/articles/using-pull-requests/) for more information.

#### Becoming a Repository Contributor

Individuals who have made many significant contributions and wish to join the core Alexandria team may contact the maintainers to request contributor status to the main Alexandria repository. The maintainers will review your application and decide whether or not granting you access to the main Alexandria repository would be beneficial to the project.

Best Practices & Coding Style
-----------------------------

All Scala code contributed to Alexandria should conform to the [Effective Scala](http://twitter.github.io/effectivescala/) guidelines. We use the [Codacy](https://www.codacy.com/app/hawk/Alexandria/dashboard) automated code review service to ensure that all contributions are Effective Scala-compliant. Any pull requests will not be merged to master until they are fully compliant with Effective Scala.

Similarly, we ask that all CoffeeScript code contributed to Alexandria conform to the [CoffeeScript Style Guide](https://github.com/polarmobile/coffeescript-style-guide).

Ideally, all pull requests should add complete unit tests for all code added. Codacy also tracks test coverage for all branches and pull requests. We prefer to not merge any pull requests that decreases the overall coverage score for the project, although exceptions may be made in some situations.


Communicating with Alexandria's Maintainers
-------------------------------------------

#### Contacting the Maintainers

If you have any questions regarding contributing to Alexandria or about the project itself, the maintainers (@hawkw and @redbassett) are happy to answer any questions. You may wish to consider joining the project's [Gitter chat channel](https://gitter.im/alexandrialibrary/Alexandria?utm_source=share-link&utm_medium=link&utm_campaign=share-link) to discuss Alexandria with the project's maintainers and other users. The Gitter chat channel contains an ongoing discussion of Alexandria development, and is a good place to go to ask questions or to recieve development updates.

#### Bug Reports and Feature Requests

If you have found any bugs in Alexandria or would like to request a feature or improvement, please feel free to open an issue on the [Alexandria issue tracker](https://github.com/alexandrialibrary/Alexandria/issues). If you are reporting a bug, please provide a complete and detailed description of the issue, a description of the environment on which Alexandria is running (i.e. your computer and operating system, Java Runtime Environment (JRE) version, Alexandria version, et cetera, web browser version), and any relevant stack traces or error logs. This will assist the maintainers in diagnosing and fixing the issue.

While the project's contributors are always happy to add additional features to Alexandria, the development team is often occupied with ongoing maintenance and implementing planned features. Therefore, feature requests may not be added right away. If you are eager to see a feature added as soon as possible, you may wish to consider forking Alexandria and adding that feature yourself. The development team loves to provide advice to contributors who wish to add new features to Alexandria.

Before opening an issue, please ensure that there is not already an open issue for the problem or feature in question. If there is an open issue already, you may wish to confirm that bug. If you do so, please include a detailed bug report, as discussed above.
