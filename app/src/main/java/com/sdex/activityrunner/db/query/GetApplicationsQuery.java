package com.sdex.activityrunner.db.query;

import android.text.TextUtils;

import com.sdex.activityrunner.db.application.ApplicationModel;
import com.sdex.activityrunner.preferences.SortingPreferences;

public class GetApplicationsQuery {

  private final String searchText;
  private final String sortBy;
  private final String sortOrder;
  private final String sortCaseSensitive;

  public GetApplicationsQuery(String searchText, SortingPreferences sortingPreferences) {
    this.searchText = searchText;
    this.sortBy = sortingPreferences.getSortBy();
    this.sortOrder = sortingPreferences.getSortOrder();
    this.sortCaseSensitive = sortingPreferences.getSortCaseSensitive();
  }

  @Override
  public String toString() {
    StringBuilder queryStringBuilder = new StringBuilder();
    queryStringBuilder.append("SELECT * FROM ").append(ApplicationModel.TABLE).append(" ");
    queryStringBuilder.append("WHERE ").append(ApplicationModel.ACTIVITIES_COUNT).append(" > 0 ");
    if (!TextUtils.isEmpty(searchText)) {
      queryStringBuilder.append(" AND ").append(ApplicationModel.NAME)
        .append(" LIKE '%").append(searchText).append("%' ");
    }
    queryStringBuilder.append("ORDER BY ").append(sortBy).append(" ")
      .append(sortCaseSensitive).append(" ").append(sortOrder);
    return queryStringBuilder.toString();
  }
}
