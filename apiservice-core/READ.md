Apiservice-core is a library that creates realtime apis for K8S . 


Api Service core is a library that create  simple rest api  with routes
    -eventbus : GET /api/{:entity-name}/eventbus
    -list :  GET /api/{:entity-name}
    -realtimeQuery :  GET /api/{:entity-name}/live
    -read :  GET /api/{:entity-name}/{:id}
    -create: POST /api/{:entity-name}
    -update :PUT /api/{:entity-name}/{:id}
    -delete : delete /api/{:entity-name}/{:id}


if we look at the user service example  we will see a "org.saltuk.users.table.UserTable.class" class annotited properties , based on this class  we design api database table like old jpa entity.  

Create, update operations are uses a json body  for multiple objects  {multiple:true, payload:array} or single object {multiple:false, payload:{}}

Based on all properties of  UserTable.class can be filtered  based on enum class "org.saltuk.core.db.query.filter.DBFilterType".

we had common filters equal , not , in , not_in . and additional filters  for String like , not_like  or   for Integers min and max filters.

for example let's  make a filter for UserTable.class   username  is not equal "saltuk" , age equals  30  , id is minimum than 35 ,  email is like ( contains) gmail 

this request to api returns an GET /api/users/?name-not=saltuk&age=30&id-min=35&email-like=gmail&pg_offset=0&pg_limit=20

ApiResult.java instance as a json  result. 


Live Query is a stream works on eventbus which triggers on database table changes. 

if user X  calls /api/users/live?name-not=saltuk&age=30&id-min=35&email-like=gmail&pg_offset=0&pg_limit=1  a Live Query will be created and returns address for Y  , token   andh result of that query.

if user Y  calls same request /api/users/live?name-not=saltuk&age=30&id-min=35&email-like=gmail&pg_offset=0&pg_limit=1  if live query of X user is active it will connect that query  and returns an another unique address and token for user Y. shortly if a query is active with same criteria of filter it will be connected 

with common address based this means for same filters created by User X and User Y  called same filters   a single LiveQuery that runs only but for each  X and  Y will connect with different  address. that makes single query for different users with same filter and secures it with personalized address. if there is no connection on live query  it will waits for time out  and will destroy itself.  (for api is now 5 minutes in activity will destroy live Query)

 /api/users/live?name-not=saltuk&age=30&id-min=35&email-like=gmail&pg_offset=0&pg_limit=1
ApiResult.java  meta propery will contains an token_live information with address of personalized eventbus address  with  header token . 

