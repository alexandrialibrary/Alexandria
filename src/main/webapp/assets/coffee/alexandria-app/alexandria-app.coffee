app = angular.module 'alexandria-app', ['mgcrea.ngStrap']

app.controller 'BookListCtrl', ['$http', ($http) ->
  booksList = this

  $http.get 'http://localhost:8080/api/books'
    .success (data, status, headers, config)->
      booksList.books = data
      console.log booksList.books

      $scope.popover = {
        "title": "Title",
        "content": "Hello Popover<br />This is a multiline message!"
      };
]
