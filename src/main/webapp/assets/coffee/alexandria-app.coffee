app = angular.module 'alexandria-app', []

app.controller 'BookListController', ($http) ->
  booksList = this

  $http.get 'http://localhost:8080/api/books'
    .success (response)->
      console.log(response)
      booksList.books = response.data
