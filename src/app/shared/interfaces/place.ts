export interface Category {
  id: number;
  name: string;
}

export interface Place {
  placeId: number;
  name: string;
  description: string;
  location: string;
  imageUrl: string;
  latitude: number;
  longitude: number;
  googleId: string;
  rating: string;
  userTotal: string;
  category: Category;
}
