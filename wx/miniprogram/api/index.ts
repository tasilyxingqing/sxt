import userApi from './system/user';
import loginApi from './system/login';
import queryApi from './wvp/query';
import pushApi from './wvp/push';
import proxyApi from './wvp/proxy';
import recordApi from './wvp/record';

class apis {
  static userApi = userApi
  static loginApi = loginApi
  static queryApi = queryApi
  static pushApi = pushApi
  static proxyApi = proxyApi
  static recordApi = recordApi
}

export default apis;