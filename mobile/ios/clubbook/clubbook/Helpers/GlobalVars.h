//
//  GlobalVars.h
//  Clubbook
//
//  Created by Andrew on 7/1/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreLocation/CoreLocation.h>

@interface GlobalVars : NSObject {
    int MaxCheckinRadius;
    int MaxFailedCheckin;
    int CheckinUpdateTime;
}

@property(nonatomic,assign)int MaxCheckinRadius;
@property(nonatomic,assign)int MaxFailedCheckin;
@property(nonatomic,assign)int CheckinUpdateTime;
+(GlobalVars*)getInstance;
@end

