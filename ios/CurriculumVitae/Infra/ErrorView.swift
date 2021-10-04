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

struct ErrorView: View {
    var body: some View {
        Text("generic_error")
            .font(.title3)
            .fontWeight(.semibold)
            .multilineTextAlignment(.center)
            .foregroundColor(Color(UIColor.init(named: "OnError") ?? UIColor.white))
            .padding()
            .background(Color(UIColor.init(named: "ErrorBackground") ?? UIColor.blue))
            .clipShape(RoundedRectangle(cornerRadius: 10))
            .padding()
    }
}

struct ErrorView_Previews: PreviewProvider {
    static var previews: some View {
        ErrorView()
    }
}

struct ErrorViewDark_Previews: PreviewProvider {
    static var previews: some View {
        ErrorView().preferredColorScheme(.dark)
    }
}
