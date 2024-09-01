export class UserModel {
  constructor(
    public id: string,
    public username: string,
    public usernameUnique: string,
    public role: string,
    public about?: string,
    public profilePictureId?: string,
    public following?: UserSimplifiedModel[],
    public followers?: UserSimplifiedModel[],
  ) {}
}

export class UserSimplifiedModel {
  constructor(
    public id: string,
    public username: string,
    public usernameUnique: string,
    public role: string,
    public about?: string | null,
    public profilePictureId?: string | null,
  ) {}
}
