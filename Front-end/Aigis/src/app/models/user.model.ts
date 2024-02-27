export class UserModel {
  constructor(
    public id: string,
    public username: string,
    public usernameUnique: string,
    public role: string,
    public about?: string,
    public profilePictureId?: string,
  ) {}
}
