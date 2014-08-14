//
//  Config.h
//  Clubbook
//
//  Created by Andrew on 8/14/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "BaseDto.h"

@interface Config : BaseDto

@property (assign, nonatomic) int maxCheckinRadius;
@property (assign, nonatomic) int maxFailedCheckin;
@property (assign, nonatomic) int checkinUpdateTime;

@end
