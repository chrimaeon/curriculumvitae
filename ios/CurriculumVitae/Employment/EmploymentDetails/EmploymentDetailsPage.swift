// Copyright (c) 2021. Christian Grach <christian.grach@cmgapps.com>
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

import SwiftUI
import common

struct EmploymentDetailsPage: View {
    let employment: Employment?
    var body: some View {
        if let employment = employment {
            EmploymentView(employment: employment).navigationBarTitle(employment.employer)
        } else {
            Text("generic_error")
        }
}
}

private struct EmploymentView: View {
    let employment: Employment

    var body: some View {
        ScrollView {
            VStack {
                Text(employment.jobTitle).font(.title)
                Text(employment.workPeriod.asHumanReadableString())
            }
        }
    }
}

struct EmploymentDetailsPage_Previews: PreviewProvider {
    static var previews: some View {
        EmploymentDetailsPage(
            employment: Employment(
                id: 1,
                jobTitle: "Software Developer",
                employer: "CMG Mobile Apps",
                startDate: Kotlinx_datetimeLocalDate(year: 2010, monthNumber: 1, dayOfMonth: 1),
                endDate: nil,
                city: "Graz",
                description: ["Line 1", "Line 2"]
            )
        )
    }
}
