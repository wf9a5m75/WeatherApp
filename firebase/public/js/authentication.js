import { signInWithPopup, GoogleAuthProvider } from 'https://www.gstatic.com/firebasejs/9.15.0/firebase-auth.js';
export { getAuth, GoogleAuthProvider } from 'https://www.gstatic.com/firebasejs/9.15.0/firebase-auth.js';

export class Authentication {

  auth = undefined;
  provider = undefined
  user = undefined;

  constructor(
    auth,
    provider,
    credentialFromResult,
    createUser,
  ) {
    this.auth = auth;
    this.provider = provider;
    this.createUser = createUser;
    this.credentialFromResult = credentialFromResult;
  }

  async signIn() {
    try {
      const result = await signInWithPopup(this.auth, this.provider);
      const credential = this.credentialFromResult(result);
      this.user = this.createUser(result.user, credential);
    } catch (error) {
      this.user = undefined;
      console.error(error);
    }
  }
}
