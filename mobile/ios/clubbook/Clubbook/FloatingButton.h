//
//  FloatingButton.h
//  Clubbook
//
//  Created by Anton Semenyuk on 2/6/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface FloatingButton : UIButton

@property BOOL statusOn;
@property (strong, nonatomic) NSString* onTitle;
@property (strong, nonatomic) NSString* offTitle;

- (void) changeStatus:(BOOL) isOn;
- (void) setOnTitle:(NSString *)onTitle andOffTitle:(NSString*)offTitle;

@end
