/*
 * Copyright (c) 2021. Christian Grach <christian.grach@cmgapps.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import SwiftUI
import common

private enum SelectedTab {
    case profile
    case employment
    case skills
    case info
}

struct ContentView: View {
    @State private var selection: SelectedTab = .profile

    var body: some View {
        TabView(selection: $selection) {
            ProfilePage(
                viewModel: ProfileViewModel(repository: CurriculumVitaeApp.koin.getProfileRepository())
            ).tabItem {
                Label("Profile",
                      systemImage: selectableImage(imageName: "person.circle", selected: selection == .profile))
            }
            .tag(SelectedTab.profile)
            EmploymentPage(
                viewModel: EmploymentViewModel(repository: CurriculumVitaeApp.koin.getEmploymentRepository())
            ).tabItem {
                Label("Employment",
                      systemImage: selectableImage(imageName: "briefcase.circle", selected: selection == .employment))
            }
            .tag(SelectedTab.employment)
            SkillsPage()
                .tabItem {
                    Label("Skills",
                          systemImage: selectableImage(imageName: "hammer.circle", selected: selection == .skills))
                }
                .tag(SelectedTab.skills)
            InfoPage()
                .tabItem {
                    Label("Info", systemImage: selectableImage(imageName: "info.circle", selected: selection == .info))
                }
                .tag(SelectedTab.info)
        }
    }
}

private func selectableImage(imageName: String, selected: Bool) -> String {
    selected ? imageName + ".fill" : imageName
}
