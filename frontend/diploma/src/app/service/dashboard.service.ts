import {Injectable} from "@angular/core";
import {HttpClient, HttpParams} from "@angular/common/http";
import {StatisticEntry} from "../model/statistic.entry";
import {DefectStatistic} from "../model/defect.statistic";

@Injectable({providedIn: 'root'})
export class DashboardService {
  constructor(private http: HttpClient) {
  }

  getUserStatistic(projectId: number) {
    let params = new HttpParams().set('projectId', projectId.toString())
    return this.http.get<StatisticEntry[]>('api/analytic/user-statistics', {params: params});
  }

  getTicketTypeStatistic(projectId: number) {
    let params = new HttpParams().set('projectId', projectId.toString())
    return this.http.get<StatisticEntry[]>('api/analytic/ticket-type-statistics', {params: params});
  }

  getDefectsStatistic(projectId: number) {
    let params = new HttpParams().set('projectId', projectId.toString())
    return this.http.get<Map<string, DefectStatistic>>('api/analytic/defects-statistics', {params: params});
  }

}