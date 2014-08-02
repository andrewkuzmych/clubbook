//
//  WorkingHour.h
//  Clubbook
//
//  Created by Andrew on 7/30/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "BaseDto.h"

@interface WorkingHour : BaseDto

@property (assign, nonatomic) int day;
@property (strong, nonatomic) NSString *status;
@property (strong, nonatomic) NSString *startTime;
@property (strong, nonatomic) NSString *endTime;
@end
