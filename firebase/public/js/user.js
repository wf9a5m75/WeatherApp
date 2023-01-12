export class User {
  isAdmin = false;
  userData = undefined;

  constructor(
    userData,
    isAdmin,
  ) {
    this.userData = userData;
    this.isAdmin = isAdmin;
    console.log(userData);
  }

  getIcon() {
    return this.userData.photoURL;
  }
}
