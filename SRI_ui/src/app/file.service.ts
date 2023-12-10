import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class FileService {
   url = 'http://localhost:8080/api/v1/files';


  constructor(private http: HttpClient) { }

  public getFiles(): Observable<any> {
    return this.http.get<any>(this.url);
}
}
