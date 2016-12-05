namespace java com.mogujie.recsys.soa.idl
namespace cpp com_mogujie_stress
struct User{
    1: required i32 id,
    2: optional string name,
    3: optional i32 age,
    4: optional string sex
}
service UserInfo {
  i32 getAge(1:i32 age),
  list<User> getUser();
}
service UserService{
  string getSex();
}