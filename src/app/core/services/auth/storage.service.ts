import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class StorageService {


  private namespace: string = 'st';

  constructor() { }
  getItem(key: string): string | null {
    if (typeof window !== 'undefined' && window.sessionStorage) {
      return sessionStorage.getItem(`${this.namespace}_${key}`);
    }
    return null;
  }

  setItem(key: string, value: string) {
    if (typeof window !== 'undefined' && window.sessionStorage) {
      sessionStorage.setItem(`${this.namespace}_${key}`, value);
    }
  }

  removeItem(key: string) {
    if (typeof window !== 'undefined' && window.sessionStorage) {
      sessionStorage.removeItem(`${this.namespace}_${key}`);
    }
  }

  clear() {
    if (typeof window !== 'undefined' && window.sessionStorage) {
      sessionStorage.clear();
    }
  }
}
