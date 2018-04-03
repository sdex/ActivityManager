package com.sdex.activityrunner.db.query;

import android.text.TextUtils;

import com.sdex.activityrunner.db.application.ApplicationModel;
import com.sdex.activityrunner.preferences.AdvancedPreferences;
import com.sdex.activityrunner.preferences.SortingPreferences;

public class GetApplicationsQuery {

  private final String searchText;
  private final String sortBy;
  private final String sortOrder;
  private final String sortCaseSensitive;
  private final boolean showNotExported;

  public GetApplicationsQuery(String searchText, SortingPreferences sortingPreferences,
                              AdvancedPreferences advancedPreferences) {
    this.searchText = searchText;
    this.sortBy = sortingPreferences.getSortBy();
    this.sortOrder = sortingPreferences.getSortOrder();
    this.sortCaseSensitive = sortingPreferences.getSortCaseSensitive();
    this.showNotExported = advancedPreferences.isShowNotExported();
  }

  @Override
  public String toString() {
    StringBuilder queryStringBuilder = new StringBuilder();
    queryStringBuilder.append("SELECT * FROM ").append(ApplicationModel.TABLE).append(" ");
    queryStringBuilder.append("WHERE ").append(ApplicationModel.ACTIVITIES_COUNT)
      .append(" > 0 ");
//    queryStringBuilder.append(" AND ").append("ActivityModel.exported");
//    if (!showNotExported) {
//      queryStringBuilder.append(" = true ");
//    }
    if (!TextUtils.isEmpty(searchText)) {
      queryStringBuilder.append(" AND ").append(ApplicationModel.NAME)
        .append(" LIKE '%").append(searchText).append("%' ");
    }
    queryStringBuilder.append("ORDER BY ").append(sortBy).append(" ")
      .append(sortCaseSensitive).append(" ").append(sortOrder);
    return queryStringBuilder.toString();
  }
}
