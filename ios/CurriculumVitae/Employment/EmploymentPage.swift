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

struct EmploymentPage: View {

    @StateObject var viewModel: EmploymentViewModel

    var body: some View {
        VStack {
            if viewModel.employments.isEmpty {
                Spacer()
                ProgressView()
                Spacer()
            } else {
                EmploymentList(employments: viewModel.employments)
            }
        }.onAppear(perform: {
            viewModel.startObservingEmployments()
        }).onDisappear(perform: {
            viewModel.cancelObservinceEmployments()
        })

    }
}

private struct EmploymentList: View {
    @State var employments: [Employment]

    var body: some View {
        ScrollView {
            LazyVStack(alignment: .leading, spacing: 10) {
                ForEach(employments, id: \.id) { employment in
                    ZStack(alignment: .leading) {
                        RoundedRectangle(cornerRadius: 10).fill(Color.white).shadow(radius: 4)
                        HStack(spacing: 10) {
                            Image(systemName: "building.2.fill")
                                .resizable()
                                .frame(width: 35, height: 35)
                                .foregroundColor(Color.white)
                                .padding()
                                .background(Circle().fill(Color.accentColor))
                            VStack(alignment: .leading) {
                                Text(employment.employer).font(.title2).fontWeight(.bold)
                                Text(employment.workPeriod.asHumanReadableString())
                                Text(employment.jobTitle).font(.title3).padding(.top, 1.0)
                            }
                        }.padding()
                    }.padding()
                }
            }
        }
    }
}
