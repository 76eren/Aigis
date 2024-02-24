export class UserModel {
  constructor(
    public id: String,
    public username: String,
    public usernameUnique: String,
    public role: String,
    public about?: String,
    public profilePictureId?: String,
  ) {}
}
