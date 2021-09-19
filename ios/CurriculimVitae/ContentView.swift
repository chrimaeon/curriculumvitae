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
    case Profile
    case Employment
    case Skills
    case Info
}

struct ContentView: View {
    var koin: Koin_coreKoin
    @State private var selection: SelectedTab = .Profile
    
    var body: some View {
        TabView(selection: $selection) {
            ProfilePage(viewModel: ProfileViewModel(repository: koin.getProfileRepository()))
                .tabItem {
                    Label("Profile", systemImage:  selectableImage(imageName: "person.circle", selected: selection == .Profile))
                }
                .tag(SelectedTab.Profile)
            EmploymentPage()
                .tabItem {
                    Label("Employment", systemImage: selectableImage(imageName: "bag", selected: selection == .Employment))
                }
                .tag(SelectedTab.Employment)
            SkilsPage()
                .tabItem {
                    Label("Skills", systemImage: selectableImage(imageName: "wrench.and.screwdriver", selected: selection == .Skills))
                    
                }
                .tag(SelectedTab.Skills)
            InfoPage()
                .tabItem {
                    Label("Info", systemImage: selectableImage(imageName: "info.circle", selected: selection == .Info))
                }
                .tag(SelectedTab.Info)
        }
    }
}

private func selectableImage(imageName: String, selected: Bool) -> String {
    selected ? imageName + ".fill" : imageName
}
