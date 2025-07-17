import {User} from './user';

export interface jwtToken {
  token: string;
  user: User;
}
