app = angular.module 'alexandria-app', []

app.controller 'BookListCtrl', ['$http', ($http) ->
  booksList = this

  $http.get 'http://localhost:8080/api/books'
    .success (data, status, headers, config)->
      booksList.books = data
      console.log booksList.books
]
