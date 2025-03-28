import {animate, keyframes, transition, trigger} from '@angular/animations';
import {Component, Input, OnInit} from '@angular/core';
import {IPlace} from '../../../shared/interfaces/IPlace';
import {Subject} from 'rxjs';
import {swipeleft, swiperight} from './keyframes';
import {NgOptimizedImage} from '@angular/common';


@Component({
  selector: 'app-card',
  imports: [
    NgOptimizedImage
  ],
  templateUrl: './card.component.html',
  styleUrl: './card.component.css',
  animations: [
    trigger('cardAnimator', [
      transition('* => swiperight', animate(750, keyframes(swiperight))),
      transition('* => swipeleft', animate(750, keyframes(swipeleft)))
    ])]
})
export class CardComponent implements OnInit {
  index = 0;
  animationState: string = '';
  @Input() parentSubject: Subject<string> = new Subject();
  @Input() places!: IPlace[];

  ngOnInit(): void {
    this.parentSubject.subscribe(event => {
      this.startAnimation(event);
      console.log(`Swipe: ${event}`);

    });
  }

  startAnimation(state: string) {
    console.log('Starting animation', this.index, this.places[this.index]);
    if (!this.animationState) {
      this.animationState = state;
    }
  }

  resetAnimationState($event: any) {
    this.animationState = '';
    if (this.index >= this.places.length) {
      this.index = 0;
    } else {
      this.index++;
    }
  }


  ngOnDestroy() {
    this.parentSubject.unsubscribe();
  }
}
