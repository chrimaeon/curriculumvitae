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

struct DebugView: View {
    @State var selectedUrl: String

    init() {
        if let selectedBaseUrl = UserDefaults.standard.object(forKey: "BaseUrl") {
            // swiftlint:disable:next force_cast
            selectedUrl = selectedBaseUrl as! String
        } else {
            selectedUrl = BuildConfigKt.BaseUrl
        }
    }

    var body: some View {
        List {
            Section(header: Text("Base Url's")) {
                ForEach(BuildConfigKt.DebugBaseUrls, id: \.self) { url in
                    Button(action: {
                        UserDefaults.standard.set(url, forKey: "BaseUrl")
                        selectedUrl = url
                    }, label: {
                        HStack {
                            Image(systemName: selectedUrl == url ? "checkmark.circle" : "circle")
                            Text(url)
                        }
                    })
                }
            }
        }
    }
}

struct DebugView_Previews: PreviewProvider {
    static var previews: some View {
        DebugView()
    }
}
