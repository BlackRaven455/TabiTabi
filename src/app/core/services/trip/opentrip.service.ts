import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
// import {environment} from '../../../environments/environment';
import {map, Observable} from 'rxjs';

const apiKey = "5ae2e3f221c38a28845f05b6095a996d9a520f72d9d609dc1dc11964";

@Injectable({
  providedIn: 'root'
})
export class OpentripService {

  private baseUrl = 'https://api.opentripmap.com/0.1/en';

  constructor(private http: HttpClient) {
  }

  getPlacesByRadius(lat: number, lon: number, radius: number = 1000) {
    return this.http.get<any>(
      `${this.baseUrl}/places/radius?radius=${radius}&lon=${lon}&lat=${lat}&apikey=${apiKey}`
    );
  }

  getData(xid: string): Observable<{ image: string, rate: number }> {
    return this.http.get<any>(`${this.baseUrl}/places/xid/${xid}?apikey=${apiKey}`)
      .pipe(map(res => ({
        image: res.preview?.source || '',
        rate: res.rate || 0
      })));
  }
}
