import {Component} from '@angular/core';
import {HeroComponent} from '../../../shared/components/hero/hero.component';
import {HowItWorksComponent} from '../../../shared/components/how-it-works/how-it-works.component';
import {AdvantagesComponent} from '../../../shared/components/advantages/advantages.component';
import {FlowExplanationComponent} from '../../../shared/components/flow-explanation/flow-explanation.component';
import {ImpressionsComponent} from '../../../shared/components/impressions/impressions.component';
import {ContactUsComponent} from '../../../shared/components/contact-us/contact-us.component';

@Component({
  selector: 'app-dashboard',
  imports: [
    HeroComponent,
    HowItWorksComponent,
    AdvantagesComponent,
    FlowExplanationComponent,
    ImpressionsComponent,
    ContactUsComponent
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent {

}
