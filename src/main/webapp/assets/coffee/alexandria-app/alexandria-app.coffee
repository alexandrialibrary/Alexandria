app = angular.module 'alexandria-app', []

app.controller 'BookListController', ($http) ->

  $http.get 'http://localhost:8080/api/books'
    .success (response)->
      console.log(response)
      this.books = response.data
